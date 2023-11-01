package GroupThree.bds.controller;

import GroupThree.bds.dtos.PropertyListingsDTO;
import GroupThree.bds.entity.ListingStatus;
import GroupThree.bds.entity.PropertyListings;
import GroupThree.bds.entity.PropertyType;
import GroupThree.bds.service.IPropertyListingsService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
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

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.prefix}/properties")
@RequiredArgsConstructor
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
                    .areaSqm((float) faker.number().randomDouble(2, 10, 1000))
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
