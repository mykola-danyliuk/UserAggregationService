package mykola.danyliuk;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User Controller", description = "APIs for managing users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "Fetches aggregated user data from multiple databases")
    @GetMapping("/users")
    public List<User> getUsers(
        @Parameter(description = "Filter by login") @RequestParam(required = false) String login,
        @Parameter(description = "Filter by first name") @RequestParam(required = false) String firstName,
        @Parameter(description = "Filter by last name") @RequestParam(required = false) String lastName) {
        return userService.getAllUsers(login, firstName, lastName);
    }
}