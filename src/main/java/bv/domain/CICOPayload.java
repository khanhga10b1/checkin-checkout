package bv.domain;

public class CICOPayload {
        private Integer type;
        private String address;
        private Double latitude;
        private Double longitude;

        public CICOPayload() {
        }

        public CICOPayload(Integer type, String address, Double latitude, Double longitude) {
            this.type = type;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }