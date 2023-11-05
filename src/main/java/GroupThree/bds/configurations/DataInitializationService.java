package GroupThree.bds.configurations;

import GroupThree.bds.entity.Role;
import GroupThree.bds.entity.User;
import GroupThree.bds.repository.RoleRepository;
import GroupThree.bds.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
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
                String encodePassword = encoder.encode("12345");
                User user = User.builder()
                        .fullName("Admin")
                        .phoneNumber("0966887766")
                        .password(encodePassword)
                        .address("Hai Phong")
                        .facebookAccountId(0)
                        .googleAccountId(0)
                        .active(true)
                        .role(adminRole)
                        .build();

                userRepository.save(user);
            }
        }
    }
}
