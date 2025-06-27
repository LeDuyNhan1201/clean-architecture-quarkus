package org.tma.intern.application;


import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.tma.intern.common.RequestDto;
import org.tma.intern.domain.User;
import org.tma.intern.domain.UserRepository;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private static final Logger log = Logger.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @WithTransaction
    @Override
    public Uni<Boolean> create(RequestDto.CreateUser request) {
            User user = new User(
                    request.username(),
                    request.password()
            );
            return userRepository.persist(user)
                    .onItem().transform(u -> true)
                    .onFailure().recoverWithItem(t -> {
                        log.error("Failed to create user", t);
                        return false;
                    });
    }

}
