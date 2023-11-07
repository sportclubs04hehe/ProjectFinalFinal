package GroupThree.bds.service.impl;

import GroupThree.bds.dtos.ProjectDTO;
import GroupThree.bds.dtos.PropertyListingsDTO;
import GroupThree.bds.entity.Projects;
import GroupThree.bds.entity.PropertyListings;
import GroupThree.bds.entity.User;
import GroupThree.bds.exceptions.AppException;
import GroupThree.bds.repository.ProjectRepository;
import GroupThree.bds.repository.PropertyListingsRepository;
import GroupThree.bds.service.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements IProjectService {

    private final ProjectRepository repository;
    private final PropertyListingsRepository listingsRepository;
    private final ModelMapper modelMapper;


    @Override
    public Projects insertNewProject(ProjectDTO dto) {

       try {
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

           if (authentication != null) {
               User user = (User) authentication.getPrincipal();

               Projects projects = Projects.builder()
                       .projectName(dto.getProjectName())
                       .developerName(dto.getDeveloperName())
                       .launchDate(dto.getLaunchDate())
                       .expectedCompletion(dto.getExpectedCompletion())
                       .amenities(dto.getAmenities())
                       .location(dto.getLocation())
                       .projectStatus(dto.getProjectStatus())
                       .user(user)
                       .build();

               if(dto.getPropertyListings() != null){
                   List<PropertyListings> propertyListings = new ArrayList<>();
                   for(PropertyListingsDTO propertyListingsDTO : dto.getPropertyListings()){
                       PropertyListings existingProperty = listingsRepository.findById(propertyListingsDTO.getId())
                               .orElseThrow(() -> new AppException(
                                       "Property with id= " + dto.getPropertyListings() + " not found", NOT_FOUND
                               ));
                       if(existingProperty != null){
                           propertyListings.add(existingProperty);
                       }else {
                           PropertyListings newPropertyListings = new PropertyListings();
                           modelMapper.map(propertyListingsDTO, newPropertyListings);
                           propertyListings.add(newPropertyListings);
                       }

                   }
                   projects.setPropertyListings(propertyListings);
               }

               return repository.save(projects);
           }
       }catch (Exception e){
           throw new AppException(e.getMessage(),BAD_REQUEST);
       }
        return null;
    }
}
