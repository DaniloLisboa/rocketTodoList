package com.danilojavarocket.todolist.task;

import com.danilojavarocket.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/createTask")
    public ResponseEntity createTask(@RequestBody TaskModel taskModel, HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início e data de término deve ser maior que a data atual");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de término deve ser posterior à data de início");
        }

        var task = taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/getTask")
    public List<TaskModel> listTasks(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var tasks = taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

    @PutMapping("/updateTask/{id}")
    public ResponseEntity updateTask(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request){
        var idUser = request.getAttribute("idUser");

        var task = taskRepository.findById(id).orElse(null);

        if (task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
        }

        if (!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este usuário não tem permissão para alterar essa tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);

        var taskUpdated = taskRepository.save(task);

        return ResponseEntity.ok().body(taskUpdated);
    }
}
