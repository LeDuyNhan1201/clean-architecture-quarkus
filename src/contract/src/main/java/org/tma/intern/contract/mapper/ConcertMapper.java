package org.tma.intern.contract.mapper;

import org.mapstruct.*;
import org.tma.intern.contract.RequestDto.ConcertRequest;
import org.tma.intern.contract.ResponseDto.ConcertResponse;
import org.tma.intern.contract.helper.TimeHelper;
import org.tma.intern.domain.entity.Concert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConcertMapper {

    ConcertResponse.Preview toDto(Concert entity);
    @AfterMapping
    default void toDto(Concert entity, @MappingTarget ConcertResponse.Preview dto) {
        dto.setShortDescription(entity.getDescription().substring(0, 50) + "...");
    }

    Concert toEntity(ConcertRequest.Body dto);

    default Instant map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneOffset.UTC).toInstant();
    }

    default String map(Instant instant) {
        return instant == null ? null : TimeHelper.format(instant, TimeHelper.yyyyMMdd_HHmmss);
    }

}