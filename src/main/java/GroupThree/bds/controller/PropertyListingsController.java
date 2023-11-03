package GroupThree.bds.controller;

import GroupThree.bds.dtos.PropertyImageDTO;
import GroupThree.bds.dtos.PropertyListingsDTO;
import GroupThree.bds.entity.ListingStatus;
import GroupThree.bds.entity.PropertyImage;
import GroupThree.bds.entity.PropertyListings;
import GroupThree.bds.entity.PropertyType;
import GroupThree.bds.exceptions.DataNotFoundException;
import GroupThree.bds.response.CountsPropertiesResponse;
import GroupThree.bds.response.PropertySearchResponse;
import GroupThree.bds.service.IPropertyListingsService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.prefix}/properties")
@RequiredArgsConstructor
@Slf4j
public class PropertyListingsController {

    private final IPropertyListingsService service;

    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") ĐÃ THỬ KHÔNG HOẠT ĐỘNG
    public ResponseEntity<?> insertProperty(
            @Valid @RequestBody PropertyListingsDTO dto,
            BindingResult result
            ){
        try {
            if (result.hasErrors()) {
                List<String> errorMessage = result.getFieldErrors()
                        .stream().map(
                                FieldError::getDefaultMessage
                        ).toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            PropertyListings newPropertyListings = service.createPropertyListings(dto);
            return new ResponseEntity<>(newPropertyListings, CREATED);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyListingsDTO dto,
            BindingResult result
    ){
        try {
            if (result.hasErrors()) {
                List<String> errorMessage = result.getFieldErrors()
                        .stream().map(
                                FieldError::getDefaultMessage
                        ).toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            PropertyListings newPropertyListings = service.updatePropertyListings(id,dto);
            return new ResponseEntity<>(newPropertyListings, OK);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(
            @PathVariable long id
    ) {
        try {
            service.deletePropertyListings(id);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getPropertyByUser(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            if (page < 1) {
                return new ResponseEntity<>("Invalid page number", BAD_REQUEST);
            }

            PageRequest pageRequest = PageRequest.of(
                    page - 1, size, // => jpa đang là 1 trừ 1 = 0 đó là quy ước của jpa
                    Sort.by("id").descending()
            );

            Page<PropertyListings> propertyListings = service.findByUserId(userId,pageRequest);

            int totalPage = propertyListings.getTotalPages();
            List<PropertyListings> propertyListingsPage = propertyListings.getContent();

            return ResponseEntity.ok(PropertySearchResponse.builder()
                    .propertyListings(propertyListingsPage)
                    .totalPage(totalPage)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> searchPropertyListings(
            @RequestParam(name = "province", required = false) String province,
            @RequestParam(name = "district", required = false) String district,
            @RequestParam(name = "commune", required = false) String commune,
            @RequestParam(name = "maxAreaSqm", required = false) Double maxAreaSqm,
            @RequestParam(name = "minAreaSqm", required = false) Double minAreaSqm,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "propertyType", required = false) PropertyType propertyType,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {

       try{
           if (page < 1) {
               return new ResponseEntity<>("Invalid page number", BAD_REQUEST);
           }

           PageRequest pageRequest = PageRequest.of(
                   page - 1, size, // => jpa đang là 1 trừ 1 = 0 đó là quy ước của jpa
                   Sort.by("id").descending()
           );

           Page<PropertyListings> propertyListings = service.searchPropertyListings(
                   province, district, commune, maxAreaSqm,minAreaSqm , maxPrice, minPrice, propertyType ,pageRequest
           );

           int totalPage = propertyListings.getTotalPages();
           List<PropertyListings> propertyListingsPage = propertyListings.getContent();

           return ResponseEntity.ok(PropertySearchResponse.builder()
                   .propertyListings(propertyListingsPage)
                   .totalPage(totalPage)
                   .build());
       }catch (Exception e){
           // Log the exception for debugging purposes
           e.printStackTrace();
           return ResponseEntity.badRequest().body("An error occurred while processing the request.");
       }

    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPropertyListings(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "description",required = false) String description
    ) {
        try{
            List<PropertyListings> results = service.findByTitleContainsOrDescriptionContains(title,description);

            if(results.isEmpty()){
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(results);

        }catch (Exception e){
            return new ResponseEntity<>( e.getMessage(),INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/search/user-property-type")
    public ResponseEntity<?> searchUserAndPropertyType(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "propertyType") PropertyType propertyType
    ) {
        try{
            List<PropertyListings> results = service.findByUserAndPropertyType(userId,propertyType);

            if (results.isEmpty()){
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(results);

        }catch (Exception e){
            return new ResponseEntity<>( e.getMessage(),INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/property-count")
    public ResponseEntity<?> getPropertyListingCounts(
            @RequestParam(value = "userId") Long userId
    ) {
        try{
            Long results = service.totalPropertyListingsByUser(userId);

            if (results <= 0){
                return new ResponseEntity<>("No record exists, record = 0", NOT_FOUND);
            }

            return ResponseEntity.ok(String.format("Total PropertyListing= " + results + " with id=" + userId));

        }catch (Exception e){
            return new ResponseEntity<>( e.getMessage(),INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/status-count-properties")
    public ResponseEntity<?> getPropertyListingCountsStatus(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "status", required = false) ListingStatus status
    ) {
        try{
            CountsPropertiesResponse results = service.countUserListingStatuses(status,userId);
            if (status == null){
                return ResponseEntity.ok(results);
            }

            return ResponseEntity.ok().body(String.format("Listing Status= " + status + " have " + results.getCount() + " record."));

        }catch (Exception e){
            return new ResponseEntity<>( e.getMessage(),INTERNAL_SERVER_ERROR);
        }

    }


    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code
    ){
        try{
            PropertyListings propertyListings = service.getByCode(code);

            if(propertyListings.getCode().isEmpty()){
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().body(propertyListings);
        }catch (Exception e){
            return new ResponseEntity<>( e.getMessage(),INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-listing-status")
    public ResponseEntity<?> getByStatus(
            @RequestParam(name = "listingStatuses") List<ListingStatus> listingStatuses,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ){
        try{
            if (page < 1) {
                return new ResponseEntity<>("Invalid page number", BAD_REQUEST);
            }

            PageRequest pageRequest = PageRequest.of(
                    page -1, size,
                    Sort.by("id").descending()
            );

            List<PropertyListings> mergedPropertyListings = new ArrayList<>();

            for (ListingStatus status : listingStatuses) {
                Page<PropertyListings> propertyListings = service.findByListingStatusIn(status, pageRequest);
                mergedPropertyListings.addAll(propertyListings.getContent());
            }

            Page<PropertyListings> resultPage = new PageImpl<>(mergedPropertyListings, pageRequest, mergedPropertyListings.size());

            if(resultPage.isEmpty()){
                return new ResponseEntity<>(NOT_FOUND);
            }else {
                return new ResponseEntity<>(resultPage, HttpStatus.OK);
            }

        }catch (Exception e){
            return new ResponseEntity<>( e.getMessage(),INTERNAL_SERVER_ERROR);
        }
    }




    @PostMapping(value = "/uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long propertyId,
            @ModelAttribute("files") List<MultipartFile> files
    ){
        try {
            PropertyListings existingProperty = service.getPropertyById(propertyId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if(files.size() > PropertyImage.MAXIMUM_IMAGES_PER_PROPERTY) {
                return ResponseEntity.badRequest().body("You can only upload a maximum of 5 photos");
            }
            List<PropertyImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if(file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                if(file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("The file size is too large, only at least 10MB");
                }
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("The file is not in the correct format and is not supported");
                }
                // Lưu file và cập nhật thumbnail trong DTO
                String filename = storeFile(file); // Thay thế hàm này với code của bạn để lưu file
                //lưu vào đối tượng product trong DB
                PropertyImage productImage = service.createProductImage(
                        existingProperty.getId(),
                        PropertyImageDTO.builder()
                                .imageUrl(filename)
                                .build()
                );
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // Kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    /** When need fake data is use*/
    @PostMapping("/generateFakePropertyListings")
    public ResponseEntity<String> generateFakePropertyListings() {
        Faker faker = new Faker();
        Set<String> uniqueCodes = new HashSet<>(); // To ensure unique codes

        for (int i = 0; i < 1000; i++) {
            String propertyCode;
            do {
                propertyCode = generateUniqueCode(faker, uniqueCodes);
            } while (service.existByCode(propertyCode));

            PropertyListingsDTO propertyListings = PropertyListingsDTO.builder()
                    .title(faker.lorem().words(3).stream().collect(Collectors.joining(" ")))
                    .code(propertyCode)
                    .description(faker.lorem().sentence())
                    .province(faker.address().state())
                    .district(faker.address().city())
                    .commune(faker.address().city())
                    .propertyType(PropertyType.values()[RandomUtils.nextInt(0, PropertyType.values().length)])
                    .price(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 1000000)))
                    .areaSqm(faker.number().randomDouble(2, 10, 1000))
                    .numberOfRooms(faker.number().numberBetween(1, 10))
                    .numberOfBathrooms(faker.number().numberBetween(1, 5))
                    .parking(faker.bool().bool())
                    .listingStatus(ListingStatus.values()[RandomUtils.nextInt(0, ListingStatus.values().length)])
                    .user((long) faker.number().numberBetween(1,4)) // Assuming you have a method to get a random user
                    .build();

            try {
                service.createPropertyListings(propertyListings);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake PropertyListings created successfully");
    }

    private String generateUniqueCode(Faker faker, Set<String> uniqueCodes) {
        String code;
        do {
            code = faker.code().asin();
        } while (uniqueCodes.contains(code));
        uniqueCodes.add(code);
        return code;
    }


}
