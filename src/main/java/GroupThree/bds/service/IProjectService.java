package GroupThree.bds.service;

import GroupThree.bds.dtos.ProjectDTO;
import GroupThree.bds.entity.Projects;

public interface IProjectService {

    Projects insertNewProject(ProjectDTO dto);
}
