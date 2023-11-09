package GroupThree.bds.configurations;

import GroupThree.bds.dtos.UserDTO;
import GroupThree.bds.entity.Role;
import GroupThree.bds.repository.RoleRepository;
import GroupThree.bds.repository.UserRepository;
import GroupThree.bds.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeRoles();
        initializeAdminUser();
    }

    private void initializeRoles() {
        if (roleRepository.findByName("ADMIN") == null) {
            roleRepository.save(new Role("ADMIN"));
        }
        if (roleRepository.findByName("USER") == null) {
            roleRepository.save(new Role("USER"));
        }
        if (roleRepository.findByName("EDITOR") == null) {
            roleRepository.save(new Role("EDITOR"));
        }

    }

    private void initializeAdminUser() throws Exception {
        if (userRepository.findByPhoneNumber("0966887766").isEmpty()) {
            Long adminRole = 1L;

            var user = UserDTO.builder()
                    .fullName("Admin")
                    .phoneNumber("0966887766")
                    .password("12345")
                    .address("Hai Phong")
                    .facebookAccountId(0)
                    .googleAccountId(0)
                    .active(true)
                    .roleId(adminRole)
                    .build();

            var created = service.createUserWithAdmin(user).getAccessToken();
            System.out.println();
            System.out.println();
            System.out.println("Admin Token= " + created);

        }
    }
}
