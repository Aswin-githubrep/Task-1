package com.example.task1.service;

import com.example.task1.model.Task;
import com.example.task1.model.TaskExecution;
import com.example.task1.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public Task saveValidated(Task task) {
        // validate command before saving
        CommandValidator.validate(task.getCommand());
        return repo.save(task);
    }

    public Optional<Task> findById(String id) {
        return repo.findById(id);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public Task executeCommand(String taskId) throws Exception {
        Task task = repo.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        // Validate before running
        CommandValidator.validate(task.getCommand());

        // Build platform-specific shell
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        ProcessBuilder pb = isWindows
                ? new ProcessBuilder("cmd.exe", "/c", task.getCommand())
                : new ProcessBuilder("bash", "-c", task.getCommand());

        Instant start = Instant.now();

        // Timeout & capture
        Process process = pb.start();

        // Read output asynchronously
        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            ExecutorService ex = Executors.newFixedThreadPool(2);
            Future<?> f1 = ex.submit(() -> reader.lines().forEach(l -> out.append(l).append(System.lineSeparator())));
            Future<?> f2 = ex.submit(() -> err.lines().forEach(l -> out.append(l).append(System.lineSeparator())));

            boolean finished = process.waitFor(10, TimeUnit.SECONDS); // 10s timeout
            if (!finished) {
                process.destroyForcibly();
                ex.shutdownNow();
                throw new TimeoutException("Command timed out after 10 seconds");
            }
            f1.get(1, TimeUnit.SECONDS);
            f2.get(1, TimeUnit.SECONDS);
            ex.shutdown();
        }

        Instant end = Instant.now();

        TaskExecution exec = new TaskExecution(start, end, out.toString().trim());
        task.getTaskExecutions().add(exec);
        return repo.save(task);
    }
}
