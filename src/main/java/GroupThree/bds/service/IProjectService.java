package GroupThree.bds.service;

import GroupThree.bds.dtos.ProjectDTO;
import GroupThree.bds.entity.Projects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IProjectService {

    Projects insertNewProject(ProjectDTO dto);

    Projects updateProject(ProjectDTO dto,Long id);

    void deleteProject (Long id);

    Page<Projects> getAllProjects(PageRequest pageRequest);
}
