package org.tma.intern.contract.ResponseDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

public class ConcertResponse {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Preview {

        Long id;

        String title;

        String shortDescription;

        String startTime;

        String endTime;

    }

}
