package GroupThree.bds.service.impl;

import GroupThree.bds.dtos.PropertyListingsDTO;
import GroupThree.bds.entity.ListingStatus;
import GroupThree.bds.entity.PropertyListings;
import GroupThree.bds.entity.PropertyType;
import GroupThree.bds.entity.User;
import GroupThree.bds.exceptions.AppException;
import GroupThree.bds.repository.PropertyListingsRepository;
import GroupThree.bds.repository.UserRepository;
import GroupThree.bds.service.IPropertyListingsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PropertyListingsImplService implements IPropertyListingsService {

    private final PropertyListingsRepository repository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public PropertyListings createPropertyListings(PropertyListingsDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null){
            User user = (User) authentication.getPrincipal();

//        Long userId = dto.getUser();
//        User user = userRepository.findById(userId).orElse(null);

            PropertyListings propertyListings = PropertyListings.builder()
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .province(dto.getProvince())
                    .district(dto.getDistrict())
                    .commune(dto.getCommune())
                    .propertyType(dto.getPropertyType())
                    .price(dto.getPrice())
                    .areaSqm(dto.getAreaSqm())
                    .numberOfRooms(dto.getNumberOfRooms())
                    .numberOfBathrooms(dto.getNumberOfBathrooms())
                    .parking(dto.getParking())
                    .listingStatus(dto.getListingStatus())
                    .build();

            if (propertyListings.getCode() == null || propertyListings.getCode().isEmpty()) {
                propertyListings.setCode(generateUniqueCode());
            }
            propertyListings.setUser(user);

            return repository.save(propertyListings);
        }
        return null;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = String.format("%06d", new Random().nextInt(999999));
        } while (repository.existsByCode(code));
        return code;
    }

    @Override
    public PropertyListings updatePropertyListings(Long id, PropertyListingsDTO dto) {
        PropertyListings existingProperty = repository.findById(id)
                .orElseThrow(() -> new AppException("Property with id= "+ id + " not found", NOT_FOUND));

        dto.setUser(existingProperty.getUser().getId());

        existingProperty.setTitle(dto.getTitle());
        existingProperty.setDescription(dto.getDescription());
        existingProperty.setProvince(dto.getProvince());
        existingProperty.setDistrict(dto.getDistrict());
        existingProperty.setCommune(dto.getCommune());
        existingProperty.setPropertyType(dto.getPropertyType());
        existingProperty.setPrice(dto.getPrice());
        existingProperty.setAreaSqm(dto.getAreaSqm());
        existingProperty.setNumberOfRooms(dto.getNumberOfRooms());
        existingProperty.setNumberOfBathrooms(dto.getNumberOfBathrooms());
        existingProperty.setParking(dto.getParking());
        existingProperty.setListingStatus(dto.getListingStatus());

        return repository.save(existingProperty);
    }

    @Override
    @Transactional
    public void deletePropertyListings(Long id) {
        PropertyListings existingProperty = repository.findById(id)
                .orElseThrow(() -> new AppException("Property with id= "+ id + " not found", NOT_FOUND));
        repository.delete(existingProperty);
    }

    @Override
    public boolean existByCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public List<PropertyListings> findByUserId(Long id) {
        return repository.findByUserId(id);
    }

    @Override
    public List<PropertyListings> findByPropertyTypeAndPriceBetween(PropertyType propertyType, BigDecimal minPrice, BigDecimal maxPrice) {
        return null;
    }

    @Override
    public List<PropertyListings> findByAreaSqmBetween(float minArea, float maxArea) {
        return null;
    }

    @Override
    public List<PropertyListings> findByListingStatusIn(List<ListingStatus> listingStatuses) {
        return null;
    }

    @Override
    public List<PropertyListings> findByUserAndListingStatus(User user, ListingStatus listingStatus) {
        return null;
    }

    @Override
    public List<PropertyListings> findByPropertyTypeAndCommune(PropertyType propertyType, String commune) {
        return null;
    }

    @Override
    public List<PropertyListings> findByTitleContainsOrDescriptionContains(String keyword) {
        return null;
    }

    @Override
    public List<PropertyListings> findByUserAndPropertyType(User user, PropertyType propertyType) {
        return null;
    }

    @Override
    public Long countByParkingTrue() {
        return null;
    }

    @Override
    public List<PropertyListings> findTopNByOrderByPriceDesc(int topN) {
        return null;
    }

    @Override
    public List<PropertyListings> findTopNByOrderByDateOfPostingDesc(int topN) {
        return null;
    }
}