package org.tma.intern.contract.ResponseDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

public class ProfileResponse {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PreviewProfile {

        Long id;

        String gender;

    }

}
