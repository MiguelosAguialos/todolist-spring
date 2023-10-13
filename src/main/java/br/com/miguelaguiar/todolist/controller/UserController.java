package br.com.miguelaguiar.todolist.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.miguelaguiar.todolist.domain.User;
import br.com.miguelaguiar.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/cadastrousuario")
    public ResponseEntity<Object> create(@RequestBody User user){
        var userFind = this.userRepository.findByUsername(user.getUsername());

        if(userFind != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já cadastrado no sistema");
        }

        var newPassword = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
        user.setPassword(newPassword);
        this.userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
