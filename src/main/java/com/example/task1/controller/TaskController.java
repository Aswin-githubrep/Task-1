package com.example.task1.controller;

import com.example.task1.model.Task;
import com.example.task1.repository.TaskRepository;
import com.example.task1.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository repo;
    private final TaskService service;

    public TaskController(TaskRepository repo, TaskService service) {
        this.repo = repo;
        this.service = service;
    }

    // GET /tasks (all)
    @GetMapping
    public List<Task> getAll() {
        return repo.findAll();
    }

    // GET /tasks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable String id) {
        Optional<Task> t = repo.findById(id);
        return t.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // PUT /tasks  (create or update); validates command
    @PutMapping
    public ResponseEntity<?> putTask(@RequestBody Task task) {
        try {
            Task saved = service.saveValidated(task);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // DELETE /tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such task");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET /tasks/search?name=abc
    @GetMapping("/search")
    public ResponseEntity<?> searchByName(@RequestParam("name") String q) {
        List<Task> list = repo.findByNameContainingIgnoreCase(q);
        if (list.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tasks found");
        return ResponseEntity.ok(list);
    }

    // PUT /tasks/{id}/execute  -> run command safely; store TaskExecution
    @PutMapping("/{id}/execute")
    public ResponseEntity<?> execute(@PathVariable String id) {
        try {
            Task updated = service.executeCommand(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(iae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Execution failed: " + e.getMessage());
        }
    }
}
