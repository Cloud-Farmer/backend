package SpringBoot.Codebase.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @Column(name = "alert_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject; // 알람 유형

    private String message; // 알람 내용

    private String status; // 알람 상태

    private String language; // 언어

    private LocalDateTime alertedTime;

    @ManyToOne
    @JoinColumn(name = "smartfarm_id")
    private SmartFarm smartFarm;
}
