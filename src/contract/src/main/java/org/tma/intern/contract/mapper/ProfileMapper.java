package org.tma.intern.contract.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tma.intern.contract.RequestDto.ProfileRequest;
import org.tma.intern.contract.ResponseDto.ProfileResponse;
import org.tma.intern.domain.entity.Profile;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper extends BaseMapper {

    ProfileResponse.PreviewProfile toDto(Profile entity);

    Profile toEntity(ProfileRequest.Update dto);

}