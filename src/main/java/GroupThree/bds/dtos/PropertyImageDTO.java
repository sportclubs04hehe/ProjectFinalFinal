package GroupThree.bds.dtos;

import GroupThree.bds.entity.PropertyListings;
import com.fasterxml.jackson.annotation.JsonProperty;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyImageDTO {
    @JsonProperty("listing_id")
    @Min(value = 1, message = "Product's ID must be > 0")
    private PropertyListings listings;

    @Size(min = 5, max = 200, message = "Image's name")
    @JsonProperty("image_url")
    private String imageUrl;
}
