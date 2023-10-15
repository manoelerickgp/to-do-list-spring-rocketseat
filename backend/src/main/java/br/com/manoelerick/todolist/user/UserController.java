package br.com.manoelerick.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping(value = "/save")
    public UserModel create(@RequestBody UserModel userModel) {
        var user = this.userRepository.findByUserName(userModel.getUserName());
        if (user != null) {
            System.out.println("User already exist.");
            return null;
        }
        return this.userRepository.save(userModel);
    }
}
