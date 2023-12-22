package bv.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginPayload {
        @JsonProperty("is_external")
        private Boolean isExternal;
        private String password;
        private String username;

        public LoginPayload() {

        }

        public LoginPayload(Boolean isExternal, String password, String username) {
            this.isExternal = isExternal;
            this.password = password;
            this.username = username;
        }

        @JsonProperty("is_external")
        public Boolean getExternal() {
            return isExternal;
        }

        public void setExternal(Boolean external) {
            isExternal = external;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

    @Override
    public String toString() {
        return "LoginPayload{" +
                "isExternal=" + isExternal +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}