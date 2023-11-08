package GroupThree.bds.controller;

import GroupThree.bds.dtos.ProjectDTO;
import GroupThree.bds.entity.ProjectStatus;
import GroupThree.bds.entity.Projects;
import GroupThree.bds.entity.PropertyListings;
import GroupThree.bds.response.PropertySearchResponse;
import GroupThree.bds.service.IProjectService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.prefix}/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final IProjectService service;


    @GetMapping
    public ResponseEntity<?> hi(){
        return ResponseEntity.ok("Say Hi from point Project!");
    }

    @PostMapping
    public ResponseEntity<?> insertProject(
            @Valid @RequestBody ProjectDTO dto,
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
            Projects newProject = service.insertNewProject(dto);
            return new ResponseEntity<>(newProject, CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO dto,
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
            Projects newProjects = service.updateProject(dto,id);
            return new ResponseEntity<>(newProjects, OK);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

//    @GetMapping("/user")
//    public ResponseEntity<?> getProjectByUser(
//            @RequestParam(name = "userId") Long userId,
//            @RequestParam(name = "page", defaultValue = "1") int page,
//            @RequestParam(name = "size", defaultValue = "10") int size
//    ) {
//        try {
//            if (page < 1) {
//                return new ResponseEntity<>("Invalid page number", BAD_REQUEST);
//            }
//
//            PageRequest pageRequest = PageRequest.of(
//                    page - 1, size, // => jpa đang là 1 trừ 1 = 0 đó là quy ước của jpa
//                    Sort.by("id").descending()
//            );
//
//            Page<PropertyListings> propertyListings = service.findByUserId(userId,pageRequest);
//
//            int totalPage = propertyListings.getTotalPages();
//            List<PropertyListings> propertyListingsPage = propertyListings.getContent();
//
//            return ResponseEntity.ok(PropertySearchResponse.builder()
//                    .propertyListings(propertyListingsPage)
//                    .totalPage(totalPage)
//                    .build());
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PostMapping("/generateFakeProjects")
    public ResponseEntity<String> generateFakeProjects() {
        Faker faker = new Faker();
        Set<String> usedNames = new HashSet<>();  // To keep track of used project names

        for (int i = 0; i < 100; i++) {
            String projectName;
            do {
                projectName = faker.company().name();
            } while (usedNames.contains(projectName) /* or check with the database for uniqueness */);
            usedNames.add(projectName);

            ProjectDTO projectDTO = new ProjectDTO();

            projectDTO.setProjectName(projectName);
            projectDTO.setDeveloperName(faker.name().fullName());
            projectDTO.setLaunchDate(LocalDate.now().minusDays(faker.number().numberBetween(1, 365 * 2)));
            projectDTO.setExpectedCompletion(LocalDate.now().plusDays(faker.number().numberBetween(1, 365 * 2)));
            projectDTO.setAmenities(Stream.of(faker.lorem().word(), faker.lorem().word()).collect(Collectors.toList()));
            projectDTO.setLocation(faker.address().fullAddress());
            projectDTO.setProjectStatus(ProjectStatus.values()[faker.random().nextInt(ProjectStatus.values().length)]);
            try {
                // Pass the DTO to your service layer for insertion
                service.insertNewProject(projectDTO);
            } catch (Exception e) {
                // Handle any exceptions thrown by the insert operation
                e.printStackTrace();
                // Depending on your error handling strategy, you might want to stop the loop
                // or log the error and continue processing.
            }
        }
        return ResponseEntity.ok("Fake projects created successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(
            @PathVariable long id
    ) {
        try {
            service.deleteProject(id);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
