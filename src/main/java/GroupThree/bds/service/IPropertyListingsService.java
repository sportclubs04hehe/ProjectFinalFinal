package GroupThree.bds.service;

import GroupThree.bds.dtos.PropertyListingsDTO;
import GroupThree.bds.entity.ListingStatus;
import GroupThree.bds.entity.PropertyListings;
import GroupThree.bds.entity.PropertyType;
import GroupThree.bds.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface IPropertyListingsService {

    PropertyListings createPropertyListings(PropertyListingsDTO dto);
    PropertyListings updatePropertyListings(Long id,PropertyListingsDTO dto);
    void deletePropertyListings (Long id);

    boolean existByCode (String code);

    List<PropertyListings> findByUserId(Long user);

    List<PropertyListings> findByPropertyTypeAndPriceBetween(
            PropertyType propertyType,
            BigDecimal minPrice,
            BigDecimal maxPrice);

    List<PropertyListings> findByAreaSqmBetween(float minArea, float maxArea);

    List<PropertyListings> findByListingStatusIn(List<ListingStatus> listingStatuses);

    // Search by User and Listing Status: Retrieve property listings created by a specific user and with a specific listing status.
    List<PropertyListings> findByUserAndListingStatus(User user, ListingStatus listingStatus);

    List<PropertyListings> findByPropertyTypeAndCommune(PropertyType propertyType, String commune);

    List<PropertyListings> findByTitleContainsOrDescriptionContains(String keyword);

    List<PropertyListings> findByUserAndPropertyType(User user, PropertyType propertyType);

    Long countByParkingTrue();

    List<PropertyListings> findTopNByOrderByPriceDesc(int topN);

    List<PropertyListings> findTopNByOrderByDateOfPostingDesc(int topN);

}
