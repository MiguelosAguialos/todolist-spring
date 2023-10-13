package br.com.miguelaguiar.todolist.controller;

import br.com.miguelaguiar.todolist.domain.Task;
import br.com.miguelaguiar.todolist.repository.TaskRepository;
import br.com.miguelaguiar.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    @PostMapping("/createtask")
    public ResponseEntity<Object> create(@RequestBody Task task, HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        task.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt())){
            return ResponseEntity.status(400).body("A data de início / término deve ser maior que a data atual");
        }

        if(task.getStartAt().isAfter(task.getEndAt())){
            return ResponseEntity.status(400).body("A data de início deve ser antes da data de término");
        }

        var newTask = this.taskRepository.save(task);
        return ResponseEntity.status(200).body(newTask);
    }

    @GetMapping("/listTasks")
    public ResponseEntity<List<Task>> list(HttpServletRequest request){
        return ResponseEntity.status(200).body(this.taskRepository.findByIdUser((UUID) request.getAttribute("idUser")));
    }

    @PutMapping("/editTask/{id}")
    public ResponseEntity<Object> update(@RequestBody Task task, HttpServletRequest request, @PathVariable UUID id){

        var newTask = this.taskRepository.findById(id).orElse(null);

        if(newTask == null){
            return ResponseEntity.status(400).body("Tarefa não encontrada");
        }

        if(!newTask.getIdUser().equals((UUID) request.getAttribute("idUser"))){
            return ResponseEntity.status(400).body("Usuário não possui permissão para editar essa tarefa");
        }

        Utils.copyNonNullProperties(task, newTask);

        this.taskRepository.save(newTask);
        return ResponseEntity.status(200).body(newTask);
    }
}
