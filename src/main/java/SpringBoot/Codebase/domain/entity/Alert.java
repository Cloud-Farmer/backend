package SpringBoot.Codebase.domain.entity;

import javax.persistence.*;

@Entity
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject; // 알람 유형

    private String message; // 알람 내용

    private String status; // 알람 상태

    private String language; // 언어

    @ManyToOne
    private SmartFarm smartFarm;
}
