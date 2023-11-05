package GroupThree.bds.controller;

import GroupThree.bds.dtos.ProjectDTO;
import GroupThree.bds.dtos.PropertyListingsDTO;
import GroupThree.bds.entity.Projects;
import GroupThree.bds.entity.PropertyListings;
import GroupThree.bds.service.IProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("${api.prefix}/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final IProjectService service;

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
}
