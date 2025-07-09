package org.tma.intern.contract.mapper;

import org.mapstruct.*;
import org.tma.intern.contract.RequestDto.ConcertRequest;
import org.tma.intern.contract.ResponseDto.ConcertResponse;
import org.tma.intern.domain.entity.Concert;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConcertMapper extends BaseMapper {

    ConcertResponse.Preview toPreviewDto(Concert entity);

    ConcertResponse.Details toDetailsDto(Concert entity);

    Concert toEntity(ConcertRequest.Body dto);

}