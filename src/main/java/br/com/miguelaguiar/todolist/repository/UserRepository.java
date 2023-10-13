package br.com.miguelaguiar.todolist.repository;

import br.com.miguelaguiar.todolist.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);
}
