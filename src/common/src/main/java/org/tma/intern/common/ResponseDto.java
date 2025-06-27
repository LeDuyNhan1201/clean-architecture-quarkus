package org.tma.intern.common;

public class ResponseDto {

    public static class PreviewUser {
        private String username;

        public PreviewUser() {
        }

        public PreviewUser(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

}
