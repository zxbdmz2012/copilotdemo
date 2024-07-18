package com.github.copilot.task.controller;

import com.github.copilot.task.entity.TaskDetail;
import com.github.copilot.task.repository.TaskDetailJpaRepository;
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

@Tag(name = "TaskDetail Controller", description = "API for managing task details")
@RestController
@RequestMapping("/taskDetails")
public class TaskDetailController {

    private final TaskDetailJpaRepository taskDetailRepository;

    @Autowired
    public TaskDetailController(TaskDetailJpaRepository taskDetailRepository) {
        this.taskDetailRepository = taskDetailRepository;
    }

    @Operation(summary = "Get all task details", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskDetail.class))),
            @ApiResponse(description = "No task details found", responseCode = "404")
    })
    @GetMapping
    public List<TaskDetail> getAllTaskDetails() {
        return taskDetailRepository.findAll();
    }

    @Operation(summary = "Get task details by task ID", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskDetail.class))),
            @ApiResponse(description = "Task details not found", responseCode = "404")
    })
    @GetMapping("/task/{taskId}")
    public List<TaskDetail> getTaskDetailsByTaskId(@PathVariable Long taskId) {
        return taskDetailRepository.findByTaskId(taskId);
    }

    @Operation(summary = "Create a new task detail", responses = {
            @ApiResponse(description = "Task detail created successfully", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskDetail.class)))
    })
    @PostMapping
    public TaskDetail createTaskDetail(@RequestBody TaskDetail taskDetail) {
        return taskDetailRepository.save(taskDetail);
    }

    @Operation(summary = "Update an existing task detail", responses = {
            @ApiResponse(description = "Task detail updated successfully", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskDetail.class))),
            @ApiResponse(description = "Task detail not found", responseCode = "404")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDetail> updateTaskDetail(@PathVariable Long id, @RequestBody TaskDetail taskDetailDetails) {
        return taskDetailRepository.findById(id)
                .map(taskDetail -> {
                    taskDetail.setNodeId(taskDetailDetails.getNodeId());
                    taskDetail.setRetryCount(taskDetailDetails.getRetryCount());
                    taskDetail.setStatus(taskDetailDetails.getStatus());
                    taskDetail.setStartTime(taskDetailDetails.getStartTime());
                    taskDetail.setEndTime(taskDetailDetails.getEndTime());
                    taskDetail.setErrorMsg(taskDetailDetails.getErrorMsg());
                    TaskDetail updatedTaskDetail = taskDetailRepository.save(taskDetail);
                    return ResponseEntity.ok(updatedTaskDetail);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a task detail", responses = {
            @ApiResponse(description = "Task detail deleted successfully", responseCode = "200"),
            @ApiResponse(description = "Task detail not found", responseCode = "404")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskDetail(@PathVariable Long id) {
        return taskDetailRepository.findById(id)
                .map(taskDetail -> {
                    taskDetailRepository.delete(taskDetail);
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}