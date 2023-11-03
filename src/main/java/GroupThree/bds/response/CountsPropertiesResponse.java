package GroupThree.bds.response;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class CountsPropertiesResponse {

    private Long pendingCount;
    private Long approvedCount;
    private Long cancelCount;

    private Long count;

    private Long totalCount;

    public CountsPropertiesResponse(Long count){
        this.count = count;
    }

}
