package GroupThree.bds.configurations;

import GroupThree.bds.entity.User;
import GroupThree.bds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpringSecurityAuthenticationFacade implements AuthenticationFacade {

    private final UserRepository userRepository;

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authentication details found in security context");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof UserDetails) {
            String phoneNumber = ((UserDetails) principal).getUsername();
            Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
            if (user.isPresent()) {
                return user.get();
            } else {
                throw new UsernameNotFoundException("User not found with phone number: " + phoneNumber);
            }
        }
        throw new IllegalStateException("The user details are not set in the security context");
    }


}
