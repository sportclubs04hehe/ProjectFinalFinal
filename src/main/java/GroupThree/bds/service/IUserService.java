package GroupThree.bds.service;

import GroupThree.bds.dtos.UpdateUserDTO;
import GroupThree.bds.dtos.UserDTO;
import GroupThree.bds.entity.User;
import GroupThree.bds.response.AuthenticationResponse;

public interface IUserService {
    AuthenticationResponse createUser(UserDTO userDTO) throws Exception;
    AuthenticationResponse createUserWithAdmin(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password, Long roleId) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

    User findByPhoneNumber (String phone);
}
