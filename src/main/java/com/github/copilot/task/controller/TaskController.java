package com.github.copilot.task.controller;

import com.github.copilot.task.entity.Task;
import com.github.copilot.task.repository.TaskJpaRepository;
import com.github.copilot.task.repository.TaskRepository;
import com.github.copilot.task.scheduler.ScheduleTaskExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Task Controller", description = "API for managing tasks")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskJpaRepository taskJpaRepository;

    private final TaskRepository taskRepository;


    @Autowired
    public TaskController(TaskJpaRepository taskJpaRepository, TaskRepository taskRepository) {
        this.taskJpaRepository = taskJpaRepository;
        this.taskRepository = taskRepository;
    }

    @Operation(summary = "Get all tasks", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Task.class))),
            @ApiResponse(description = "No tasks found", responseCode = "404")
    })
    @GetMapping
    public List<Task> getAllTasks() {
        return taskJpaRepository.findAll();
    }

    @Operation(summary = "Get a task by ID", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Task.class))),
            @ApiResponse(description = "Task not found", responseCode = "404")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskJpaRepository.findById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new task", responses = {
            @ApiResponse(description = "Task created successfully", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Task.class)))
    })
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskJpaRepository.save(task);
    }

    @Operation(summary = "Update an existing task", responses = {
            @ApiResponse(description = "Task updated successfully", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Task.class))),
            @ApiResponse(description = "Task not found", responseCode = "404")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskJpaRepository.findById(id)
                .map(task -> {
                    task.setName(taskDetails.getName());
                    task.setCronExpr(taskDetails.getCronExpr());
                    task.setStatus(taskDetails.getStatus());
                    task.setNodeId(taskDetails.getNodeId());
                    task.setSuccessCount(taskDetails.getSuccessCount());
                    task.setFailCount(taskDetails.getFailCount());
                    task.setInvokeInfoJson(taskDetails.getInvokeInfoJson());
                    task.setFirstStartTime(taskDetails.getFirstStartTime());
                    task.setNextStartTime(taskDetails.getNextStartTime());
                    task.setFinalEndTime(taskDetails.getFinalEndTime());
                    Task updatedTask = taskJpaRepository.save(task);
                    return ResponseEntity.ok(updatedTask);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a task", responses = {
            @ApiResponse(description = "Task deleted successfully", responseCode = "200"),
            @ApiResponse(description = "Task not found", responseCode = "404")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        return taskJpaRepository.findById(id)
                .map(task -> {
                    taskJpaRepository.delete(task);
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Import statements and other class content...

    @Autowired
    private ScheduleTaskExecutor  taskExecutor; // Assuming you have a TaskExecutor bean

    @Operation(summary = "Start a task immediately", responses = {
            @ApiResponse(description = "Task started successfully", responseCode = "200"),
            @ApiResponse(description = "Task not found", responseCode = "404")
    })
    @PostMapping("/{id}/startNow")
    public ResponseEntity<?> startTaskNow(@PathVariable Long id) {
        Optional<Task> taskOptional = taskJpaRepository.findById(id);
        if (!taskOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Task task = taskOptional.get();
        taskExecutor.startNow(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reinitialize all tasks", responses = {
            @ApiResponse(description = "Tasks reinitialized successfully", responseCode = "200"),
            @ApiResponse(description = "Operation failed", responseCode = "500")
    })
    @PostMapping("/reinitAll")
    public ResponseEntity<?> reinitAllTasks() {
        try {
            int reinitializedTasksCount = taskRepository.reInitTasks();
            return ResponseEntity.ok().body(reinitializedTasksCount + " tasks reinitialized.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reinitialize tasks.");
        }
    }

    @Operation(summary = "Reinitialize tasks for a specific node", responses = {
            @ApiResponse(description = "Tasks reinitialized successfully for the specified node", responseCode = "200"),
            @ApiResponse(description = "Operation failed", responseCode = "500")
    })
    @PostMapping("/reinit/{nodeId}")
    public ResponseEntity<?> reinitTasksForNode(@PathVariable String nodeId) {
        try {
            int reinitializedTasksCount = taskRepository.reInitTasks(nodeId);
            return ResponseEntity.ok().body(reinitializedTasksCount + " tasks reinitialized for node " + nodeId + ".");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reinitialize tasks for node " + nodeId + ".");
        }
    }

}