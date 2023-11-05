package GroupThree.bds.repository;

import GroupThree.bds.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
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
            "AND (:propertyType is null or p.propertyType = :propertyType)" +
            "AND (:realEstateType is null or p.realEstateType = :realEstateType)")
    Page<PropertyListings> searchPropertyListings(
            @Param("province") String province,
            @Param("district") String district,
            @Param("commune") String commune,
            @Param("maxAreaSqm") Double maxAreaSqm,
            @Param("minAreaSqm") Double minAreaSqm,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minPrice") BigDecimal minPrice,
            @Param("propertyType") PropertyType propertyType,
            @Param("realEstateType") RealEstateType realEstateType,
            Pageable pageable
    );

    boolean existsByCode (String code);

    Page<PropertyListings> findByUserId (Long id, PageRequest pageRequest);

    List<PropertyListings> findByPropertyType (PropertyType propertyType);

    PropertyListings findByCode (String code);

    // Search by ListingStatus
    Page<PropertyListings> findByListingStatus(ListingStatus listingStatus, PageRequest pageRequest);

    @Query("select p from PropertyListings p left join fetch p.productImages where p.id = :propertyId")
    Optional<PropertyListings> getDetailProperty(@Param("propertyId") Long propertyId);

    // Search by price, sorted from high to low
    List<PropertyListings> findByOrderByPriceDesc();

    // Search by price, sorted from low to high
    List<PropertyListings> findByOrderByPriceAsc();

    List<PropertyListings> findByTitleContainsIgnoreCaseOrDescriptionContainsIgnoreCase(
            @Param("title") String title,
            @Param("description") String description
    );

    List<PropertyListings> findByUserIdAndPropertyType(Long userId, PropertyType propertyType);

    // Define additional custom queries as needed
    @Query("SELECT p FROM PropertyListings p WHERE p.propertyType = :customValue")
    List<PropertyListings> findByCustomCriteria(@Param("customValue") String customValue);

    Long countPropertyListingsByUserId (Long userId);

    Long countByListingStatusAndUserId(ListingStatus listingStatus, Long userId);
    @Query("SELECT COUNT(p) FROM PropertyListings p WHERE p.listingStatus = 'PENDING'")
    Long countWaitingForApprovalPropertyListings();

    @Query("SELECT COUNT(p) FROM PropertyListings p WHERE p.listingStatus = 'APPROVED'")
    Long countApprovedPropertyListings();

    @Query("SELECT COUNT(p) FROM PropertyListings p WHERE p.listingStatus = 'CANCEL'")
    Long countCanceledPropertyListings();

}
