package org.tma.intern.contract.mapper;

import org.mapstruct.*;
import org.tma.intern.contract.RequestDto.ConcertRequest;
import org.tma.intern.contract.ResponseDto.ConcertResponse;
import org.tma.intern.domain.entity.Concert;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConcertMapper extends BaseMapper {

    ConcertResponse.Preview toDto(Concert entity);
    @AfterMapping
    default void toDto(Concert entity, @MappingTarget ConcertResponse.Preview dto) {
        dto.setShortDescription(entity.getDescription().substring(0, 50) + "...");
    }

    Concert toEntity(ConcertRequest.Body dto);

}