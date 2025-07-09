package org.tma.intern.contract.RequestDto;

import org.tma.intern.domain.enums.Region;

import java.time.LocalDateTime;

public class ConcertRequest {

    public record Body(
            String title,
            String description,
            String location,
            Region region,
            LocalDateTime startTime,
            LocalDateTime endTime
    ){};

}
