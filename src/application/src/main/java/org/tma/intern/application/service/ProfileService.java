package org.tma.intern.application.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.tma.intern.contract.RequestDto.ProfileRequest;
import org.tma.intern.contract.ResponseDto.ProfileResponse;
import org.tma.intern.domain.entity.Profile;

public interface ProfileService {

    Uni<Long> create(Profile entity);

    Uni<Long> update(Long id, ProfileRequest.Update entity);

    Uni<ProfileResponse.PreviewProfile> findById(Long id);

    Multi<Long> seedData(int count);

}
