CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       login VARCHAR(50) NOT NULL,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50)
);

INSERT INTO users (login, first_name, last_name) VALUES
                                                     ('jdoe', 'John', 'Doe'),
                                                     ('asmith', 'Anna', 'Smith');