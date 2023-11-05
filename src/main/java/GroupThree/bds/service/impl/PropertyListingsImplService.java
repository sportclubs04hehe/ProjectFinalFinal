package GroupThree.bds.service.impl;

import GroupThree.bds.dtos.PropertyImageDTO;
import GroupThree.bds.dtos.PropertyListingsDTO;
import GroupThree.bds.entity.*;
import GroupThree.bds.exceptions.AppException;
import GroupThree.bds.exceptions.DataNotFoundException;
import GroupThree.bds.exceptions.InvalidParamException;
import GroupThree.bds.repository.PropertyImageRepository;
import GroupThree.bds.repository.PropertyListingsRepository;
import GroupThree.bds.repository.UserRepository;
import GroupThree.bds.response.CountsPropertiesResponse;
import GroupThree.bds.service.IPropertyListingsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final PropertyImageRepository imageRepository;
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
                    .realEstateType(dto.getRealEstateType())
                    .price(dto.getPrice())
                    .areaSqm(dto.getAreaSqm())
                    .numberOfRooms(dto.getNumberOfRooms())
                    .numberOfBathrooms(dto.getNumberOfBathrooms())
                    .parking(dto.getParking())
                    .listingStatus(ListingStatus.PENDING)
//                    .listingStatus(dto.getListingStatus())
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
    public PropertyListings updatePropertyListings(
            Long id,
            PropertyListingsDTO dto
    ) {
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
    public Page<PropertyListings> findByUserId(Long id,PageRequest pageRequest) {
        return repository.findByUserId(id,pageRequest);
    }

    @Override
    public Page<PropertyListings> searchPropertyListings(
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
    ) {
        return repository.searchPropertyListings(
                province, district, commune, maxAreaSqm, minAreaSqm , maxPrice, minPrice, propertyType, realEstateType, pageRequest
        );
    }

    @Override
    public PropertyListings getByCode(String code){
        if(!existByCode(code)){
           throw new AppException("Code =" + code +" not found",NOT_FOUND);
        }
        return repository.findByCode(code);
    }

    @Override
    public Page<PropertyListings> findByListingStatusIn(
            ListingStatus listingStatuses,
            PageRequest pageRequest
    ) {
        Page<PropertyListings> propertyListings = repository.findByListingStatus(listingStatuses,pageRequest);
        if(propertyListings.isEmpty()){
            throw new AppException("No property listings found with the given listing statuses.", NOT_FOUND);
        }
        return propertyListings;
    }

    @Override
    public PropertyListings getPropertyById(Long id) throws DataNotFoundException {
        Optional<PropertyListings> propertyListings = repository.getDetailProperty(id);
        if(propertyListings.isPresent()){
            return propertyListings.get();
        }
        throw new DataNotFoundException("Cannot find product with id =" + id);
    }

    @Override
    public PropertyImage createProductImage(
            Long propertyId,
            PropertyImageDTO dto
    ) throws Exception {
        PropertyListings existingProperty = repository.findById(propertyId)
                .orElseThrow(() -> new AppException("Property with id= "+ propertyId + " not found", NOT_FOUND));

        PropertyImage newPropertyImage = PropertyImage.builder()
                .listings(existingProperty)
                .imageUrl(dto.getImageUrl())
                .build();

        //Ko cho insert quá 5 ảnh cho 1 sản phẩm
        int size = imageRepository.findByListingsId(propertyId).size();
        if(size >= PropertyImage.MAXIMUM_IMAGES_PER_PROPERTY){
            throw new InvalidParamException(
                    "Number of images must be <= "
                            +PropertyImage.MAXIMUM_IMAGES_PER_PROPERTY);
        }
        return imageRepository.save(newPropertyImage);
    }

    @Override
    public List<PropertyListings> findByUserAndListingStatus(User user, ListingStatus listingStatus) {
        return null;
    }


    @Override
    public List<PropertyListings> findByTitleContainsOrDescriptionContains(
            String title,
            String description
    ) {
        return repository.findByTitleContainsIgnoreCaseOrDescriptionContainsIgnoreCase(title,description);
    }

    @Override
    public List<PropertyListings> findByUserAndPropertyType(Long userId, PropertyType propertyType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User with id= "+ userId + " not found", NOT_FOUND));
        return repository.findByUserIdAndPropertyType(user.getId(),propertyType);
    }

    @Override
    public Long totalPropertyListingsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User with id= "+ userId + " not found", NOT_FOUND));
        return repository.countPropertyListingsByUserId(user.getId());
    }

    @Override
    public CountsPropertiesResponse countUserListingStatuses(ListingStatus status, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User with id= "+ userId + " not found", NOT_FOUND));

        if(status != null){
            Long count = repository.countByListingStatusAndUserId(status,userId);
            return new CountsPropertiesResponse(count);

        }else {
            Long totalCount = repository.countPropertyListingsByUserId(user.getId());
            Long pendingCount = repository.countByListingStatusAndUserId(ListingStatus.PENDING,user.getId());
            Long approvedCount = repository.countByListingStatusAndUserId(ListingStatus.APPROVED,user.getId());
            Long cancelCount = repository.countByListingStatusAndUserId(ListingStatus.CANCEL, user.getId());
            return CountsPropertiesResponse.builder()
                    .pendingCount(pendingCount)
                    .approvedCount(approvedCount)
                    .cancelCount(cancelCount)
                    .totalCount(totalCount)
                    .count(null)
                    .build();
        }
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
