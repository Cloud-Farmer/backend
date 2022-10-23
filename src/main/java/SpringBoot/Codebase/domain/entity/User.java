package SpringBoot.Codebase.domain.entity;


import lombok.*;
import org.influxdb.annotation.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="testuser")
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(name ="farmname")
    private String farmname;
    @Column(name ="username")
    private String username;
    @Column(name ="identity")
    private String identity;
    @Column(name ="password")
    private String password;
}