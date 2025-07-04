package org.tma.intern.domain.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.tma.intern.domain.entity.Profile;

@ApplicationScoped
public class ProfileRepository implements PanacheRepositoryBase<Profile, Long> {

}
