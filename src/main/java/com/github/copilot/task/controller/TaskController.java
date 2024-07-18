package com.github.copilot.task.controller;

import com.github.copilot.task.entity.Task;
import com.github.copilot.task.repository.TaskJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Task Controller", description = "API for managing tasks")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskJpaRepository taskRepository;

    @Autowired
    public TaskController(TaskJpaRepository taskRepository) {
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
        return taskRepository.findAll();
    }

    @Operation(summary = "Get a task by ID", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Task.class))),
            @ApiResponse(description = "Task not found", responseCode = "404")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new task", responses = {
            @ApiResponse(description = "Task created successfully", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Task.class)))
    })
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    @Operation(summary = "Update an existing task", responses = {
            @ApiResponse(description = "Task updated successfully", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Task.class))),
            @ApiResponse(description = "Task not found", responseCode = "404")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setName(taskDetails.getName());
                    task.setCronExpr(taskDetails.getCronExpr());
                    task.setStatus(taskDetails.getStatus());
                    task.setNodeId(taskDetails.getNodeId());
                    task.setSuccessCount(taskDetails.getSuccessCount());
                    task.setFailCount(taskDetails.getFailCount());
                    task.setInvokeInfo(taskDetails.getInvokeInfo());
                    task.setFirstStartTime(taskDetails.getFirstStartTime());
                    task.setNextStartTime(taskDetails.getNextStartTime());
                    task.setFinalEndTime(taskDetails.getFinalEndTime());
                    Task updatedTask = taskRepository.save(task);
                    return ResponseEntity.ok(updatedTask);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a task", responses = {
            @ApiResponse(description = "Task deleted successfully", responseCode = "200"),
            @ApiResponse(description = "Task not found", responseCode = "404")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}