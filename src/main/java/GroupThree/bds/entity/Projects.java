package GroupThree.bds.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "_project")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Projects extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "project_name")
    private String projectName; // ten

    @Column(name = "developer_name")
    private String developerName; // chu dau tu

    @Column(name = "launch_date")
    private Date launchDate; // ngay ra mat

    @Column(name = "expected_completion")
    private Date expectedCompletion; // ky vong hoan thanh

    @Column(name = "amenities")
    private String amenities; // tien nghi

    @Column(length = 500, name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private ProjectStatus projectStatus;

    @OneToMany(mappedBy = "projects", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PropertyListings> propertyListings;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
