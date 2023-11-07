package GroupThree.bds.service.impl;

import GroupThree.bds.configurations.AuthenticationFacade;
import GroupThree.bds.dtos.ProjectDTO;
import GroupThree.bds.entity.Projects;
import GroupThree.bds.entity.User;
import GroupThree.bds.exceptions.AppException;
import GroupThree.bds.repository.ProjectRepository;
import GroupThree.bds.repository.UserRepository;
import GroupThree.bds.service.IProjectService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements IProjectService {

    private final ProjectRepository repository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationFacade authenticationFacade;

    @Override
    @Transactional
    public Projects insertNewProject(ProjectDTO dto) {
        Projects projects = modelMapper.map(dto,Projects.class);

        String currentPhoneNumberUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByPhoneNumber(currentPhoneNumberUser);

        projects.setUser(currentUser);
        return repository.save(projects);

//        if (dto.getUserId() == null) {
//            throw new IllegalArgumentException("User ID is null");
//        }
//
//        User user = userRepository.findById(dto.getUserId())
//                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + dto.getUserId()));
//
//        Projects project = modelMapper.map(dto, Projects.class);
//        project.setUser(user); // Assign the found user to the project
//
//        return repository.save(project);
    }

    @Override
    public Projects updateProject(ProjectDTO dto, Long id) {
        Projects existingProject  = repository.findById(id)
                .orElseThrow(() -> new AppException("Project with id= "+ id + " not found", NOT_FOUND));

        User currentUser = authenticationFacade.getCurrentUser();

        if (!existingProject.getUser().getId().equals(currentUser.getId())) {
            throw new AppException("User does not have permission to update this project", UNAUTHORIZED);
        }

        if (!existingProject.getProjectName().equals(dto.getProjectName())) {
            throw new AppException("Project name cannot be changed",BAD_REQUEST);
        }

        existingProject.setProjectName(dto.getProjectName());
        existingProject.setDeveloperName(dto.getDeveloperName());
        existingProject.setLaunchDate(dto.getLaunchDate());
        existingProject.setExpectedCompletion(dto.getExpectedCompletion());
        existingProject.setAmenities(dto.getAmenities());
        existingProject.setLocation(dto.getLocation());
        existingProject.setProjectStatus(dto.getProjectStatus());

        return repository.save(existingProject);
    }

    @Override
    public void deleteProject(Long id) {
        Projects existingProject  = repository.findById(id)
                .orElseThrow(() -> new AppException("Project with id= "+ id + " not found", NOT_FOUND));

        User currentUser = authenticationFacade.getCurrentUser();


        if (!existingProject.getUser().getId().equals(currentUser.getId())) {
            throw new AppException("User does not have permission to update this project", UNAUTHORIZED);
        }

        repository.delete(existingProject);
    }

}
