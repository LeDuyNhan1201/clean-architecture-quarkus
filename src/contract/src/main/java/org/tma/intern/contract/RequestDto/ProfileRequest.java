package org.tma.intern.contract.RequestDto;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.tma.intern.domain.enums.Gender;

import java.util.Date;

public class ProfileRequest {

    public record Update(
        Gender gender,
        Date dayOfBirth
    ){};

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Avatar {

        @FormParam("image")
        @PartType(MediaType.MULTIPART_FORM_DATA)
        FileUpload image;

        @FormParam("fileName")
        @PartType(MediaType.TEXT_PLAIN)
        String fileName;

    }

}
