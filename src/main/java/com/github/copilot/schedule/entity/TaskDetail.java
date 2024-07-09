package com.github.copilot.schedule.entity;

import com.github.copilot.db.BaseEntity;
import com.github.copilot.schedule.enums.TaskStatus;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "schedule_task_detail")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class TaskDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "schedule_task_detail_seq")
    @SequenceGenerator(name = "schedule_task_detail_seq",
            sequenceName = "schedule_task_detail_seq",
            allocationSize = 1)
    private Long id;

    @Version
    private Integer version;

    /**
     * 任务id
     */
    private Long taskId;


    /**
     * 当前执行的节点id
     */
    private String nodeId;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 状态，0表示待执行，1表示执行中，2表示异常中，3表示已完成
     * 添加了任务明细说明就开始执行了
     */
    @Enumerated(EnumType.ORDINAL)
    private TaskStatus status = TaskStatus.DOING;

    /**
     * 开始时间
     */
    private Date startTime = new Date();

    /**
     * 结束时间
     */
    private Date endTime;


    /**
     * 错误信息
     */
    private String errorMsg;

    public TaskDetail(Long taskId) {
        this.taskId = taskId;
    }
}
