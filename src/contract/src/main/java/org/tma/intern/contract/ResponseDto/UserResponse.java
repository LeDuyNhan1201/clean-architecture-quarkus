package org.tma.intern.contract.ResponseDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

public class UserResponse {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PreviewUser {

        String id;

        String username;

    }

}
