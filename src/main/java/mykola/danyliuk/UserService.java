package mykola.danyliuk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    private final Map<DataSourcePropertiesList.DataSourceProperties, Connection> connections = new HashMap<>();

    @Autowired
    public UserService(DataSourcePropertiesList dataSourcePropertiesList) {
        for (DataSourcePropertiesList.DataSourceProperties dataSourceProperties : dataSourcePropertiesList.dataSources()) {
            try {
                Connection connection = DriverManager.getConnection(dataSourceProperties.url(), dataSourceProperties.user(), dataSourceProperties.password());
                connections.put(dataSourceProperties, connection);
                log.info("Connected to {}", dataSourceProperties.name());
            } catch (Exception e) {
                log.error("Error connecting to {}", dataSourceProperties.name(), e);
                throw new RuntimeException("Error initializing datasource: " + dataSourceProperties.name(), e);
            }
        }
    }

    public List<User> getAllUsers() {
        if (connections.isEmpty()) {
            return List.of();
        }
        List<User> allUsers = new ArrayList<>();
        for (Map.Entry<DataSourcePropertiesList.DataSourceProperties, Connection> entry : connections.entrySet()) {
            DataSourcePropertiesList.DataSourceProperties dataSourceProperties = entry.getKey();
            try (Connection connection = entry.getValue();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM " + dataSourceProperties.table())) {

                int count = 0;

                while (resultSet.next()) {
                    User user = mapUser(resultSet, dataSourceProperties.mapping());
                    allUsers.add(user);
                    count++;
                }

                log.info("Fetched {} users from {}", count, dataSourceProperties.name());

            } catch (Exception e) {
                log.error("Error fetching users from {}", dataSourceProperties.name(), e);
            }
        }
        return allUsers;
    }

    private User mapUser(ResultSet resultSet, Map<String, String> mapping) throws Exception {
        String id = resultSet.getString(mapping.get("id"));
        String username = resultSet.getString(mapping.get("username"));
        String name = resultSet.getString(mapping.get("name"));
        String surname = resultSet.getString(mapping.get("surname"));
        return new User(id, username, name, surname);
    }
}