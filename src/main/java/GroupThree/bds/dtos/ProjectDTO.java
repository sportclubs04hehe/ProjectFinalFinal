package GroupThree.bds.dtos;

import GroupThree.bds.entity.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class ProjectDTO {

    @NotBlank(message = "Project Name is required")
    @Size(max = 200, message = "Project Name must be less than 200 characters")
    @JsonProperty("project_name")
    private String projectName; // ten

    @NotBlank(message = "Developer Name is required")
    @Size(max = 100, message = "Developer Name must be less than 100 characters")
    @JsonProperty("developer_name")
    private String developerName; // chu dau tu

    @JsonProperty("launch_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate launchDate; // ngay ra mat

    @JsonProperty("expected_completion")
    @Future(message = "The expected completion date must be in the future.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate expectedCompletion; // ky vong hoan thanh

    @NotEmpty(message = "Amenities cannot be empty")
    @JsonProperty("amenities")
    private List<String> amenities; // tien nghi

    @NotEmpty(message = "Location cannot be empty")
    @Size(max = 500, message = "Location must be less than 500 characters")
    @JsonProperty("location")
    private String location;

    @NotNull(message = "Project Status is required")
    @JsonProperty("project_status")
    private ProjectStatus projectStatus;

    @JsonProperty("user_id")
    private Long userId;

}
