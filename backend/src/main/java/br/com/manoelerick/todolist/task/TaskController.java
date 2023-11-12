package br.com.manoelerick.todolist.task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {

    private final ITaskRepository taskRepository;

    public TaskController(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        System.out.println("Chegou no controller!");
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        /* validar se a data passada na task é maior que a data atual
        pois não faz sentido uma task ter inicio numa data que já passou */
        var currantDate = LocalDateTime.now();
        // Exemplo:
        // 10/11/2023 - current(atual)
        // 10/10/2023 - startAt(inicio)
        if (currantDate.isAfter(taskModel.getStartAt()) || currantDate.isAfter(taskModel.getEndAt())) {
            // Se a data atual é depois da data da task, então:
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de inicio / data de término deve ser maior que a data atual");
        }
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            // Se a data de inicio vier depois da minha data de término, será retornado um erro
            return ResponseEntity.status(400).body("A data de inicio deve ser anterior a data de término");
        }

        return ResponseEntity.status(200).body(this.taskRepository.save(taskModel));
    }
}
