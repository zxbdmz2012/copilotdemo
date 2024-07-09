package com.github.copilot.exception.entity;

import com.github.copilot.db.BaseEntity;
import com.github.copilot.util.DateUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Entity
@Table(name = "exception_info")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ExceptionInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "exception_info_seq")
    @SequenceGenerator(name = "exception_info_seq",
            sequenceName = "exception_info_seq",
            allocationSize = 1)
    private Long id;

    @Length(max = 50)
    private String message;

    @Lob
    private String stackTrace;

    private String simpleName;

    private String time;

    public ExceptionInfo(LocalDateTime localDateTime, Class errorType, Throwable throwable) {
        this.time = DateUtil.date2str(localDateTime);
        this.simpleName = errorType.getSimpleName();
        this.message = throwable.getMessage();
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new java.io.PrintWriter(sw));
        StringBuffer sb = sw.getBuffer();
        this.stackTrace = sb.toString();
    }
}
