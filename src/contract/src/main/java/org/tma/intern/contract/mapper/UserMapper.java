package org.tma.intern.contract.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tma.intern.contract.RequestDto.UserRequest;
import org.tma.intern.contract.ResponseDto.UserResponse;
import org.tma.intern.domain.entity.IdentityUser;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BaseMapper {

    UserResponse.PreviewUser toDto(IdentityUser entity);

    IdentityUser toEntity(UserRequest.Creation dto);

    IdentityUser toEntity(UserRequest.UpdateBody dto);

}