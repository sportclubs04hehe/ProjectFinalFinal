package GroupThree.bds.dtos;

import GroupThree.bds.entity.ListingStatus;
import GroupThree.bds.entity.PropertyType;
import GroupThree.bds.entity.RealEstateType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PropertyListingsDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 200, message = "Title must be between 10 and 200 characters")
    private String title;

    @Size(min = 6, max = 6, message = "Mã phải có 6 ký tự")
    private String code;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description not too 1000 character")
    private String description;

    @NotBlank(message = "Province is required")
    @Size(max = 100, message = "Province not too 200 character")
    private String province;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District not too 200 character")
    private String district;

    @NotBlank(message = "Commune is required")
    @Size(max = 100, message = "Commune not too 100 character")
    private String commune;


    @JsonProperty("property_type")
    @NotNull( message = "Property Type is required")
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @JsonProperty("real_estate_type")
    @NotNull( message = "Real Estate Type Type is required")
    @Enumerated(EnumType.STRING)
    private RealEstateType realEstateType;

    @Positive(message = "Price must be greater than 0")
    @NotNull( message = "Price is required")
    private BigDecimal price;

    @JsonProperty("area_sqm")
    @NotNull( message = "areaSqm is required")
    @PositiveOrZero(message = "Area Sqm must be zero or positive")
    private Double areaSqm;

    @PositiveOrZero(message = "Số phòng không thể là số âm")
    private Integer numberOfRooms; // số phòng

    @PositiveOrZero(message = "Số phòng tắm không thể là số âm")
    private Integer numberOfBathrooms; // số phòng tắm

    private Boolean parking; // chỗ đậu xe

    @JsonProperty("listing_status")
    private ListingStatus listingStatus;

    @JsonProperty("user_id")
    private Long user;


}
