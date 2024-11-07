package mykola.danyliuk;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "data-sources")
public record DataSourcePropertiesList(DataSourceProperties[] dataSources) {
    public record DataSourceProperties(
        String name, String strategy, String url, String table,
        String user, String password, Map<String, String> mapping) {

        public String idMapping() {
            return mapping.get("id");
        }

        public String usernameMapping() {
            return mapping.get("username");
        }

        public String nameMapping() {
            return mapping.get("name");
        }

        public String surnameMapping() {
            return mapping.get("surname");
        }

    }
}