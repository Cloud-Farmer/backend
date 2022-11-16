package SpringBoot.Codebase.domain.entity;

import javax.persistence.*;

@Entity
public class SmartFarm { // 회원 FK, 농장주

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensors;

    private String condition1;

    private String condition2;

    private String condition3;

    private String condition4;

}
