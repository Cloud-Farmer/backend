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

    private String messageKR; // 알람 내용

    private String messageENG; // 알람 내용

    private String status; // 알람 상태


    private LocalDateTime alertedTime;  // 시각

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smartfarm_id")
    private SmartFarm smartFarm;
}
