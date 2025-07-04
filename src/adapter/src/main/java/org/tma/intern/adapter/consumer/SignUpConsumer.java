package org.tma.intern.adapter.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.tma.intern.application.service.ProfileService;
import org.tma.intern.application.service.UserService;
import org.tma.intern.domain.entity.Profile;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SignUpConsumer {

    ProfileService profileService;

    UserService userService;

    public void onMessage(@Observes Profile message) {
        profileService.create(message).subscribe().with(
                res -> {},
                failure -> log.error("Error in profile creation or compensation", failure)
            );
    }


}
