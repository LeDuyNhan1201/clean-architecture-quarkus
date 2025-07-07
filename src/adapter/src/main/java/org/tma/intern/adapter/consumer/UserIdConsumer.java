package org.tma.intern.adapter.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.tma.intern.application.service.ProfileService;
import org.tma.intern.domain.entity.Profile;

@Slf4j
@ApplicationScoped
public class UserIdConsumer {

    @Inject
    ProfileService profileService;

    @Incoming("user-id-in")
    public Uni<Void> consumeUserId(String userId) {
        log.info("Received userId from Kafka: {}", userId);

        Profile profile = Profile.builder().userId(userId).build();

        return profileService.create(profile)
                .invoke(profileId -> log.info("Created profile with id: {}", profileId))
                .replaceWithVoid();
    }
}
