package org.tma.intern.contract.RequestDto;

import java.time.LocalDateTime;

public class ConcertRequest {

    public record Body(
            String title,
            String description,
            String location,
            LocalDateTime startTime,
            LocalDateTime endTime
    ){};

}
