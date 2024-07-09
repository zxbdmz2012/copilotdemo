package com.github.copilot.schedule.scheduler;

import com.github.copilot.schedule.common.Invocation;
import com.github.copilot.schedule.config.EasyJobConfig;
import com.github.copilot.schedule.entity.Node;
import com.github.copilot.schedule.entity.Task;
import com.github.copilot.schedule.entity.TaskDetail;
import com.github.copilot.schedule.enums.NotifyCmd;
import com.github.copilot.schedule.enums.TaskStatus;
import com.github.copilot.schedule.repository.NodeRepository;
import com.github.copilot.schedule.repository.TaskRepository;
import com.github.copilot.schedule.strategy.Strategy;
import com.github.copilot.schedule.utils.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 任务调度器
 */
@Component
public class TaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TaskExecutor.class);
    /**
     * 创建任务到期延时队列
     */
    private final DelayQueue<DelayItem<Task>> taskQueue = new DelayQueue<>();
    /**
     * 可以明确知道最多只会运行2个线程，直接使用系统自带工具就可以了
     */
    private final ExecutorService bossPool = Executors.newFixedThreadPool(2);
    /**
     * 正在执行的任务的Future
     */
    private final Map<Long, Future> doingFutures = new HashMap<>();
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private EasyJobConfig config;
    /**
     * 声明工作线程池
     */
    private ThreadPoolExecutor workerPool;

    /**
     * 获取任务的策略
     */
    private Strategy strategy;


    @PostConstruct
    public void init() {
        /**
         * 根据配置选择一个节点获取任务的策略
         */
        strategy = Strategy.choose(config.getNodeStrategy());
        /**
         * 自定义线程池，初始线程数量corePoolSize，线程池等待队列大小queueSize，当初始线程都有任务，并且等待队列满后
         * 线程数量会自动扩充最大线程数maxSize，当新扩充的线程空闲60s后自动回收.自定义线程池是因为Executors那几个线程工具
         * 各有各的弊端，不适合生产使用
         */
        workerPool = new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaxPoolSize(), 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(config.getQueueSize()));
        /**
         * 执行待处理任务加载线程
         */
        bossPool.execute(new Loader());
        /**
         * 执行任务调度线程
         */
        bossPool.execute(new Boss());

    }

    /**
     * 完成子任务，如果父任务失败了，子任务不会执行
     *
     * @param task
     * @param detail
     * @throws Exception
     */
    private void finish(Task task, TaskDetail detail) throws Exception {

        //当没有子任务时完成父任务
        taskRepository.finish(task, detail);


    }

    /**
     * 添加任务
     *
     * @param name
     * @param cronExp
     * @param invockor
     * @return
     * @throws Exception
     */
    public long addTask(String name, String cronExp, Invocation invockor) throws Exception {
        Task task = new Task(name, cronExp, invockor);
        return taskRepository.insert(task);
    }

    /**
     * 立即执行任务，就是设置一下延时为0加入任务队列就好了，这个可以外部直接调用
     *
     * @param taskId
     * @return
     */
    public boolean startNow(Long taskId) {
        Task task = taskRepository.get(taskId);
        task.setStatus(TaskStatus.DOING);
        taskRepository.updateTask(task);
        DelayItem<Task> delayItem = new DelayItem<Task>(0L, task);
        return taskQueue.offer(delayItem);
    }

    /**
     * 立即停止正在执行的任务，留给外部调用的方法
     *
     * @param taskId
     * @return
     */
    public boolean stopNow(Long taskId) {
        Task task = taskRepository.get(taskId);
        if (task == null) {
            return false;
        }
        /**
         * 该任务不是正在执行，直接修改task状态为已完成即可
         */
        if (task.getStatus() != TaskStatus.DOING) {
            task.setStatus(TaskStatus.STOP);
            taskRepository.updateTask(task);
            return true;
        }
        /**
         * 该任务正在执行，使用节点配合心跳发布停用通知
         */
        int n = nodeRepository.updateNotifyInfo(config.getNodeId(), NotifyCmd.STOP_TASK, String.valueOf(taskId));
        return n > 0;
    }

    /**
     * 立即停止正在执行的任务，这个不需要自己调用，是给心跳线程调用
     *
     * @param taskId
     * @return
     */
    public boolean stop(Long taskId) {
        Task task = taskRepository.get(taskId);
        /**
         * 不是自己节点的任务，本节点不能执行停用
         */
        if (task == null || !config.getNodeId().equals(task.getNodeId())) {
            return false;
        }
        /**
         * 拿到正在执行任务的future，然后强制停用，并删除doingFutures的任务
         */
        Future future = doingFutures.get(taskId);
        boolean flag = future.cancel(true);
        if (flag) {
            doingFutures.remove(taskId);
            /**
             * 修改状态为已停用
             */
            task.setStatus(TaskStatus.STOP);
            taskRepository.updateTask(task);
        }
        /**
         * 重置通知信息，避免重复执行停用通知
         */
        nodeRepository.resetNotifyInfo(config.getNodeId(), NotifyCmd.STOP_TASK);
        return flag;
    }

    class Loader implements Runnable {

        @Override
        public void run() {
            for (; ; ) {
                try {
                    /**
                     * 先休息一下
                     */
                    Thread.sleep(config.getFetchPeriod());
                    /**
                     * 先获取可用的节点列表
                     */
                    List<Node> nodes = nodeRepository.getEnableNodes(config.getHeartBeatSeconds() * 2);
                    if (nodes == null || nodes.isEmpty()) {
                        continue;
                    }
                    /**
                     * 查找还有指定时间(单位秒)才开始的主任务列表
                     */
                    List<Task> tasks = taskRepository.listNotStartedTasks(config.getFetchDuration());
                    if (tasks == null || tasks.isEmpty()) {
                        continue;
                    }
                    for (Task task : tasks) {

                        boolean accept = strategy.accept(nodes, task, config.getNodeId());
                        /**
                         * 不该自己拿就不要抢
                         */
                        if (!accept) {
                            continue;
                        }
                        /**
                         * 先设置成待执行
                         */
                        task.setStatus(TaskStatus.PENDING);
                        task.setNodeId(config.getNodeId());
                        /**
                         * 使用乐观锁尝试更新状态，如果更新成功，其他节点就不会更新成功。如果其它节点也正在查询未完成的
                         * 任务列表和当前这段时间有节点已经更新了这个任务，version必然和查出来时候的version不一样了,这里更新
                         * 必然会返回0了
                         */
                        Date nextStartTime = task.getNextStartTime();
                        if (taskRepository.updateTask(task) || nextStartTime == null) {
                            continue;
                        }

                        /**
                         * 如果任务的下次启动时间还在系统启动时间之前，说明时间已过期需要重新更新
                         */
                        if (nextStartTime != null && nextStartTime.before(config.getSysStartTime())) {
                            /**
                             * 如果服务停止重新启动后由于之前的任务的nextStartTime时间还是之前的就可能存在，再次启动后仍然按照之前时间执行的情况
                             */
                            CronExpression cronExpession = new CronExpression(task.getCronExpr());
                            Date nextStartDate = cronExpession.getNextValidTimeAfter(config.getSysStartTime());
                            task.setNextStartTime(nextStartDate);
                            task.setStatus(TaskStatus.NOT_STARTED);
                            taskRepository.updateTask(task);
                            continue;
                        }

                        /**
                         * 封装成延时对象放入延时队列,这里再查一次是因为上面乐观锁已经更新了版本，会导致后面结束任务更新不成功
                         */
                        task = taskRepository.get(task.getId());
                        DelayItem<Task> delayItem = new DelayItem<Task>(nextStartTime.getTime() - new Date().getTime(), task);
                        taskQueue.offer(delayItem);

                    }

                } catch (Exception e) {
                    logger.error("fetch task list failed,cause by:{}", e);
                }
            }
        }

    }

    class Boss implements Runnable {
        @Override
        public void run() {
            for (; ; ) {
                try {
                    /**
                     * 时间到了就可以从延时队列拿出任务对象,然后交给worker线程池去执行
                     */
                    DelayItem<Task> item = taskQueue.take();
                    if (item != null && item.getItem() != null) {
                        Task task = item.getItem();
                        /**
                         * 真正开始执行了设置成执行中
                         */
                        task.setStatus(TaskStatus.DOING);
                        /**
                         * loader线程中已经使用乐观锁控制了，这里没必要了
                         */
                        taskRepository.updateTask(task);
                        /**
                         * 提交到线程池
                         */
                        Future future = workerPool.submit(new Worker(task));
                        /**
                         * 暂存在doingFutures
                         */
                        doingFutures.put(task.getId(), future);
                    }

                } catch (Exception e) {
                    logger.error("fetch task failed,cause by:{}", e);
                }
            }
        }

    }

    class Worker implements Callable<String> {

        private final Task task;

        public Worker(Task task) {
            this.task = task;
        }

        @Override
        public String call() {
            logger.info("Begin to execute task:{}", task.getId());
            TaskDetail detail = null;
            try {
                //开始任务
                detail = taskRepository.start(task);
                if (detail == null) return null;
                //执行任务
                task.getInvokor().invoke();
                //完成任务
                finish(task, detail);
                logger.info("finished execute task:{}", task.getId());
                /**
                 * 执行完后删了
                 */
                doingFutures.remove(task.getId());
            } catch (Exception e) {
                logger.error("execute task:{} error,cause by:{}", task.getId(), e);
                try {
                    taskRepository.fail(task, detail, e.getCause().getMessage());
                } catch (Exception e1) {
                    logger.error("fail task:{} error,cause by:{}", task.getId(), e);
                }
            }
            return null;
        }

    }
}
