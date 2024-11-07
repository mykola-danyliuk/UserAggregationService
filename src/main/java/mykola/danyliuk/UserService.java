package mykola.danyliuk;

import lombok.extern.slf4j.Slf4j;
import mykola.danyliuk.DataSourcePropertiesList.DataSourceProperties;
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

    private final Map<DataSourceProperties, Connection> connections = new HashMap<>();

    @Autowired
    public UserService(DataSourcePropertiesList dataSourcePropertiesList) {
        for (DataSourceProperties dataSourceProperties : dataSourcePropertiesList.dataSources()) {
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

    public List<User> getAllUsers(String login, String firstName, String lastName) {
        if (connections.isEmpty()) {
            return List.of();
        }
        List<User> allUsers = new ArrayList<>();
        for (Map.Entry<DataSourceProperties, Connection> entry : connections.entrySet()) {
            DataSourceProperties dataSourceProperties = entry.getKey();
            Connection connection = entry.getValue();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(buildQuery(dataSourceProperties, login, firstName, lastName))) {

                int count = 0;

                while (resultSet.next()) {
                    User user = mapUser(resultSet, dataSourceProperties);
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

    private String buildQuery(DataSourceProperties properties, String login, String firstName, String lastName) {
        StringBuilder query = new StringBuilder("SELECT * FROM ").append(properties.table()).append(" WHERE 1=1");
        if (login != null && !login.isEmpty()) {
            query.append(" AND ").append(properties.usernameMapping()).append(" = '").append(login).append("'");
        }
        if (firstName != null && !firstName.isEmpty()) {
            query.append(" AND ").append(properties.nameMapping()).append(" = '").append(firstName).append("'");
        }
        if (lastName != null && !lastName.isEmpty()) {
            query.append(" AND ").append(properties.surnameMapping()).append(" = '").append(lastName).append("'");
        }
        return query.toString();
    }

    private User mapUser(ResultSet resultSet, DataSourceProperties properties) throws Exception {
        String id = resultSet.getString(properties.idMapping());
        String username = resultSet.getString(properties.usernameMapping());
        String name = resultSet.getString(properties.nameMapping());
        String surname = resultSet.getString(properties.surnameMapping());
        return new User(id, username, name, surname);
    }
}