package com.alexrosa.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexrosa.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository iTaskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request) {
        System.out.println("--------> chegou no controller");

        //recupera IdUser passado pelo doFilter
        var idUser = request.getAttribute("IdUser");
        task.setIdUser((UUID)idUser);

        //valida Data
        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio ou de termino deve ser maior que a data atual!");
        }


        if (task.getStartAt().isAfter(task.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser menor que a data de termino!");
        }

        this.iTaskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("IdUser");
        var tasks = this.iTaskRepository.findByIdUser((UUID)idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel task, HttpServletRequest request, @PathVariable UUID id) {
        var idUser = request.getAttribute("IdUser");
        var taskFromDB = this.iTaskRepository.findById(id).orElseThrow();

        if (taskFromDB == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa nao encontrada");
        }
        
        if (!taskFromDB.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Uasuario nao tem permissao para alterar essa task");
        }

        Utils.copyNonNullProperties(task, taskFromDB);
        var updatedTask = this.iTaskRepository.save(taskFromDB);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

}
