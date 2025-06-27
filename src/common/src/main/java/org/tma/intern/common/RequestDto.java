package org.tma.intern.common;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class RequestDto {

    public record CreateUser(
            String username,
            String password
    ){};

    public record UpdateUser(
            String username,
            String password
    ){};

    public static class UpdateAvatar {

        @FormParam("image")
        @PartType(MediaType.MULTIPART_FORM_DATA)
        public FileUpload image;

        @FormParam("fileName")
        @PartType(MediaType.TEXT_PLAIN)
        public String fileName;

    }

}
