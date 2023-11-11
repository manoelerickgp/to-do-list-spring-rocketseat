package br.com.manoelerick.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity create(@RequestBody UserModel userModel) {
        // Buscando userName para validação(verificar se já existe e permitir o save)
        var user = this.userRepository.findByUserName(userModel.getUserName());
        // caso o userName já exista, será retornada uma mensagem dizendo que o userName já existe
        if (user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exist.");
        }
         /* Usando BCrypt para pegar hash de senha, neste caso, o 'hashToString' está sendo usado para
         criar um hash da senha do usuário, como parâmetros eu defino o 'cost' quanto maior o número
         maior a segurança do hash, porém, o processo de hash é mais lento, e a senha do usuário para ser
         criptografada, no final, é retornado uma string que representa o hash da senha do usuário */
        var passwordHashred = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashred);

        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }
}
