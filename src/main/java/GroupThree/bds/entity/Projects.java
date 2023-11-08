package GroupThree.bds.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "_project", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_name"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Projects extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "project_name")
    private String projectName; // ten

    @Column(name = "developer_name")
    private String developerName; // chu dau tu

    @Column(name = "launch_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate launchDate; // ngay ra mat

    @Future(message = "Expected completion date must be in the future")
    @Column(name = "expected_completion")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate expectedCompletion; // ky vong hoan thanh

    @ElementCollection
    @CollectionTable(name = "project_amenities", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "amenities")
    private List<String> amenities; // tien nghi

    @Column(length = 500, name = "location",nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private ProjectStatus projectStatus;

    @OneToMany(mappedBy = "projects", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PropertyListings> propertyListings;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;

}
