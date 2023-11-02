package GroupThree.bds.response;

import GroupThree.bds.entity.PropertyListings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PropertySearchResponse {
    private List<PropertyListings> propertyListings;
    private int totalPage;
}
