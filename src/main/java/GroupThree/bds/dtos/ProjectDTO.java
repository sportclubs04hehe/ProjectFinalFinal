package GroupThree.bds.dtos;

import GroupThree.bds.entity.ProjectStatus;
import GroupThree.bds.entity.PropertyListings;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class ProjectDTO {

    @NotBlank(message = "Project Name is required")
    @Size(min = 10, max = 200, message = "Project Name must be between 10 and 200 characters")
    @JsonProperty("project_name")
    private String projectName; // ten

    @NotBlank(message = "Developer Name is required")
    @Size(min = 10, max = 100, message = "Developer Name must be between 10 and 100 characters")
    @JsonProperty("developer_name")
    private String developerName; // chu dau tu

    @JsonProperty("launch_date")
    @NotBlank(message = "Launch Date Name is required")
    private Date launchDate; // ngay ra mat

    @JsonProperty("expected_completion")
    @NotBlank(message = "Expected Completion is required")
    @Future(message = "The expected completion date must be in the future.")
    private Date expectedCompletion; // ky vong hoan thanh

    @Size(min = 10, max = 200, message = "Location must be between 10 and 200 characters")
    @JsonProperty("amenities")
    private String amenities; // tien nghi

    @NotBlank(message = "Location is required")
    @Size(min = 10, max = 500, message = "Location must be between 10 and 500 characters")
    @JsonProperty("location")
    private String location;

    @Enumerated(EnumType.STRING)
    @JsonProperty("project_status")
    @NotNull(message = "Project Status is required")
    private ProjectStatus projectStatus;

    @JsonProperty("property_listings")
    private Long propertyListings;

    @JsonProperty("user_id")
    private Long userId;

}
