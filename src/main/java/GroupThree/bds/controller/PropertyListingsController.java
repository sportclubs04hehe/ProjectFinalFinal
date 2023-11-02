package GroupThree.bds.controller;

import GroupThree.bds.dtos.PropertyListingsDTO;
import GroupThree.bds.entity.ListingStatus;
import GroupThree.bds.entity.PropertyListings;
import GroupThree.bds.entity.PropertyType;
import GroupThree.bds.response.PropertySearchResponse;
import GroupThree.bds.service.IPropertyListingsService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public ResponseEntity<?> deleteProperty(@PathVariable long id) {
        try {
            service.deletePropertyListings(id);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getPropertyByUser(@PathVariable long id) {
        try {
            List<PropertyListings> propertyListings = service.findByUserId(id);
            return new ResponseEntity<>(propertyListings,OK);
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
            @RequestParam(name = "propertyType", required = false, defaultValue = "") String propertyType,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {

       try{
           if (page < 1) {
               return new ResponseEntity<>("Invalid page number", BAD_REQUEST);
           }

           PropertyType enumProperType = PropertyType.valueOf(propertyType);

           PageRequest pageRequest = PageRequest.of(
                   page - 1, size, // => jpa đang là 1 trừ 1 = 0 đó là quy ước của jpa
                   Sort.by("id").ascending()
           );

           Page<PropertyListings> propertyListings = service.searchPropertyListings(
                   province, district, commune, maxAreaSqm,minAreaSqm , maxPrice, minPrice, enumProperType ,pageRequest
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
