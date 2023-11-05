package GroupThree.bds.service;

import GroupThree.bds.dtos.PropertyImageDTO;
import GroupThree.bds.dtos.PropertyListingsDTO;
import GroupThree.bds.entity.*;
import GroupThree.bds.exceptions.DataNotFoundException;
import GroupThree.bds.response.CountsPropertiesResponse;
import GroupThree.bds.response.PropertySearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

public interface IPropertyListingsService {

    PropertyListings createPropertyListings(PropertyListingsDTO dto);
    PropertyListings updatePropertyListings(Long id,PropertyListingsDTO dto);
    void deletePropertyListings (Long id);

    boolean existByCode (String code);

    Page<PropertyListings> findByUserId(Long user,PageRequest pageRequest);

    Page<PropertyListings> searchPropertyListings(
            String province,
            String district,
            String commune,
            Double maxAreaSqm,
            Double minAreaSqm,
            BigDecimal maxPrice,
            BigDecimal minPrice,
            PropertyType propertyType,
            RealEstateType realEstateType,
            PageRequest pageRequest
    );

    PropertyListings getByCode(String code);

    Page<PropertyListings> findByListingStatusIn(ListingStatus listingStatuses,PageRequest pageRequest);

    PropertyListings getPropertyById(Long id) throws DataNotFoundException;

    PropertyImage createProductImage(
            Long propertyId,
            PropertyImageDTO dto) throws Exception;

    // Search by User and Listing Status: Retrieve property listings created by a specific user and with a specific listing status.
    List<PropertyListings> findByUserAndListingStatus(User user, ListingStatus listingStatus);

    List<PropertyListings> findByTitleContainsOrDescriptionContains(String title,String description);

    List<PropertyListings> findByUserAndPropertyType(Long userId, PropertyType propertyType);

    Long totalPropertyListingsByUser(Long userId);

    CountsPropertiesResponse countUserListingStatuses(ListingStatus status, Long userId);

    List<PropertyListings> findTopNByOrderByPriceDesc(int topN);

    List<PropertyListings> findTopNByOrderByDateOfPostingDesc(int topN);

}
