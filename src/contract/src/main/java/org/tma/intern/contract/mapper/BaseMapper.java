package org.tma.intern.contract.mapper;

import org.tma.intern.contract.helper.TimeHelper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public interface BaseMapper {

    default Instant map(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneOffset.UTC).toInstant();
    }

    default String map(Instant instant) {
        return instant == null ? null : TimeHelper.format(instant, TimeHelper.yyyyMMdd_HHmmss);
    }

}
