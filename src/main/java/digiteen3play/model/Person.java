package digiteen3play.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Person extends AbstractModel{

    @Embedded
    private PersonInfo personInfo;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    private String role;

    @CreationTimestamp
    @Column(name = "joinTime")
    private LocalDateTime joinTime;

    @Column(name = "trace_id", nullable=false, length=64, updatable=false)
    private String traceId;

}
