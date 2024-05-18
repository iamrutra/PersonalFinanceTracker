package pl.arturchub.PersonalFinanceTracker.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.arturchub.PersonalFinanceTracker.models.User;
import pl.arturchub.PersonalFinanceTracker.services.UserService;

import java.util.Optional;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        Optional<User> optionalUser = userService.findById(user.getId());

        if (user.getUsername().isEmpty()) {
            errors.rejectValue("username", "", "Username should not be empty");
        }
        if (user.getPassword().isEmpty()) {
            errors.rejectValue("password", "", "Password should not be empty");
        }
        if (user.getEmail().isEmpty()) {
            errors.rejectValue("email", "", "Email address should not be empty");
        }


        if (user.getUsername().length() < 4 || user.getUsername().length() > 20) {
            errors.rejectValue("username", "", "Username should be between 4 and 20 characters");
        }
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "", "Password should be between 8 and 32 characters");
        }


    }
}
