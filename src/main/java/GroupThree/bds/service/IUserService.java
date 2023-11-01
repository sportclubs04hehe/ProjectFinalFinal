package GroupThree.bds.service;

import GroupThree.bds.dtos.UpdateUserDTO;
import GroupThree.bds.dtos.UserDTO;
import GroupThree.bds.entity.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password, Long roleId) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;
}
