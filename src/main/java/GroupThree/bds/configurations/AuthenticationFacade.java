package GroupThree.bds.configurations;

import GroupThree.bds.entity.User;
import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {
    Authentication getAuthentication();

    User getCurrentUser();

}
