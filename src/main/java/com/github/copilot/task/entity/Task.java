package com.github.copilot.task.entity;

import com.github.copilot.db.BaseEntity;
import com.github.copilot.task.common.Invocation;
import com.github.copilot.task.enums.TaskStatus;
import com.github.copilot.task.serializer.JdkSerializationSerializer;
import com.github.copilot.task.utils.CronExpression;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.text.ParseException;
import java.util.Date;

@Entity
@Table(name = "schedule_task")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "schedule_task_seq")
    @SequenceGenerator(name = "schedule_task_seq",
            sequenceName = "schedule_task_seq",
            allocationSize = 1)
    private Long id;


    /**
     * 调度名称
     */
    private String name;

    /**
     * cron表达式
     */
    private String cronExpr;

    /**
     * 当前执行的节点id
     */
    private String nodeId;

    /**
     * 状态，0表示未开始，1表示待执行，2表示执行中，3表示已完成
     */
    @Enumerated(EnumType.ORDINAL)
    private TaskStatus status = TaskStatus.NOT_STARTED;

    /**
     * 成功次数
     */
    private Integer successCount = 0;

    /**
     * 失败次数
     */
    private Integer failCount = 0;

    /**
     * 执行信息
     */
    private byte[] invokeInfoJson;

    @Version
    private Integer version;
    /**
     * 首次开始时间
     */
    private Date firstStartTime;

    /**
     * 下次开始时间
     */
    private Date nextStartTime;


    /**
     * 下次开始时间
     */
    private Date finalEndTime;
    /**
     * 任务的执行者
     */
    @Transient
    private Invocation invocation;


    public Task(String name, String cronExpr, Invocation invocation) throws ParseException {
        this.name = name;
        this.cronExpr = cronExpr;
        this.invocation = invocation;
    }

}
