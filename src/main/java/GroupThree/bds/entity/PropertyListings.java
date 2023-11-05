package GroupThree.bds.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "_property_listings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyListings extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(unique = true, nullable = false, length = 6)
    private String code;

    @Column(length = 1000)
    private String description;

    @Column(length = 100)
    private String province; // tỉnh

    @Column(length = 50)
    private String district;

    @Column(length = 100)
    private String commune; // xã

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type")
    private PropertyType propertyType;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "area_sqm", nullable = false)
    private Double areaSqm;

    private Integer numberOfRooms; // số phòng
    private Integer numberOfBathrooms; // số phòng tắm
    private Boolean parking; // chỗ đậu xe

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status")
    private ListingStatus listingStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "real_estate_type")
    private RealEstateType realEstateType;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "listings", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PropertyImage> productImages;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Projects projects;

}
