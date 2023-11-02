package GroupThree.bds.repository;

import GroupThree.bds.entity.ListingStatus;
import GroupThree.bds.entity.PropertyListings;
import GroupThree.bds.entity.PropertyType;
import GroupThree.bds.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyListingsRepository extends JpaRepository<PropertyListings,Long> {
    // loc theo tung phan
    @Query("SELECT p FROM PropertyListings p " +
            "WHERE (:province is null or p.province = :province) " +
            "AND (:district is null or p.district= :district) " +
            "AND (:commune is null or p.commune = :commune) " +
            "AND (:maxAreaSqm is null or p.areaSqm <= :maxAreaSqm) " +
            "AND (:minAreaSqm is null or p.areaSqm >= :minAreaSqm) " +
            "AND (:maxPrice is null or p.price <= :maxPrice) " +
            "AND (:minPrice is null or p.price >= :minPrice) " +
            "AND (:propertyType is null or p.propertyType = :propertyType)")
    Page<PropertyListings> searchPropertyListings(
            @Param("province") String province,
            @Param("district") String district,
            @Param("commune") String commune,
            @Param("maxAreaSqm") Double maxAreaSqm,
            @Param("minAreaSqm") Double minAreaSqm,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minPrice") BigDecimal minPrice,
            @Param("propertyType") PropertyType propertyType,
            Pageable pageable
    );

    boolean existsByCode (String code);

    List<PropertyListings> findByUserId (Long id);

    List<PropertyListings> findByPropertyType (PropertyType propertyType);

    PropertyListings findByCode (String code);

    // Search by ListingStatus
    Page<PropertyListings> findByListingStatus(ListingStatus listingStatus, PageRequest pageRequest);

    // Search by price, sorted from high to low
    List<PropertyListings> findByOrderByPriceDesc();

    // Search by price, sorted from low to high
    List<PropertyListings> findByOrderByPriceAsc();

    // Search by multiple criteria (example: ListingStatus and PropertyType)
    List<PropertyListings> findByListingStatusAndPropertyType(ListingStatus listingStatus, PropertyType propertyType);

    // Define additional custom queries as needed
    @Query("SELECT p FROM PropertyListings p WHERE p.propertyType = :customValue")
    List<PropertyListings> findByCustomCriteria(@Param("customValue") String customValue);

    @Query("SELECT COUNT(p) FROM PropertyListings p WHERE p.user.id = :userId")
    Long countPropertyListingsByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM PropertyListings p WHERE p.listingStatus = 'PENDING'")
    Long countWaitingForApprovalPropertyListings();

    @Query("SELECT COUNT(p) FROM PropertyListings p WHERE p.listingStatus = 'APPROVED'")
    Long countApprovedPropertyListings();

    @Query("SELECT COUNT(p) FROM PropertyListings p WHERE p.listingStatus = 'CANCEL'")
    Long countCanceledPropertyListings();

}
