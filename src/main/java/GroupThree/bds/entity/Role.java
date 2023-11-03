package GroupThree.bds.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "_role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public static String ADMIN = "ADMIN";
    public static String USER = "USER";

    public Role(String name) {
        this.name = name;
    }
}
