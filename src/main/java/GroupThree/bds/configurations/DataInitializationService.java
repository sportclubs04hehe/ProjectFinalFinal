package GroupThree.bds.configurations;

import GroupThree.bds.entity.Role;
import GroupThree.bds.entity.User;
import GroupThree.bds.repository.RoleRepository;
import GroupThree.bds.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
    }

    private void initializeRoles() {
        if (roleRepository.findByName("ADMIN") == null) {
            roleRepository.save(new Role("ADMIN"));
        }
        if (roleRepository.findByName("EDITOR") == null) {
            roleRepository.save(new Role("EDITOR"));
        }
        if (roleRepository.findByName("USER") == null) {
            roleRepository.save(new Role("USER"));
        }
    }

    private void initializeAdminUser() {
        if (userRepository.findByPhoneNumber("0966887766").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN");

            if (adminRole != null) {
                User adminUser = new User();
                adminUser.setFullName("Group3Admin");
                adminUser.setPhoneNumber("0966887766");
                adminUser.setPassword("12345");
                adminUser.setAddress("Ha Noi");
                adminUser.setFacebookAccountId(0);
                adminUser.setGoogleAccountId(0);
                adminUser.setActive(true);
                adminUser.setRole(adminRole);
                // Set other user attributes

                userRepository.save(adminUser);
            }
        }
    }
}
