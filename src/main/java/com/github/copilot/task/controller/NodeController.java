package com.github.copilot.task.controller;

import com.github.copilot.task.entity.Node;
import com.github.copilot.task.repository.NodeJpaRepository;
import com.github.copilot.task.repository.NodeRepository;
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
@Tag(name = "Node Controller", description = "API for managing nodes")
@RestController
@RequestMapping("/nodes")
public class NodeController {

    private final NodeJpaRepository nodeJpaRepository;

    private final NodeRepository nodeRepository;


    @Autowired
    public NodeController(NodeJpaRepository nodeJpaRepository,NodeRepository nodeRepository) {
        this.nodeJpaRepository = nodeJpaRepository;
        this.nodeRepository = nodeRepository;
    }

    @Operation(summary = "Get all nodes", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Node.class))),
            @ApiResponse(description = "No nodes found", responseCode = "404")
    })
    @GetMapping
    public List<Node> getAllNodes() {
        return nodeJpaRepository.findAll();
    }

    @Operation(summary = "Get a node by ID", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Node.class))),
            @ApiResponse(description = "Node not found", responseCode = "404")
    })
    @GetMapping("/{nodeId}")
    public ResponseEntity<Node> getNodeById(@PathVariable String nodeId) {
        Optional<Node> node = nodeJpaRepository.findById(nodeId);
        return node.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new node", responses = {
            @ApiResponse(description = "Node created successfully", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Node.class)))
    })
    @PostMapping
    public Node createNode(@RequestBody Node node) {
        return nodeJpaRepository.save(node);
    }


    @Operation(summary = "Update an existing node", responses = {
            @ApiResponse(description = "Node updated successfully", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Node.class))),
            @ApiResponse(description = "Node not found", responseCode = "404")
    })
    @PutMapping("/{nodeId}")
    public ResponseEntity<Node> updateNode(@PathVariable String nodeId, @RequestBody Node nodeDetails) {
        return nodeJpaRepository.findById(nodeId)
                .map(node -> {
                    node.setNodeStatus(nodeDetails.getNodeStatus());
                    node.setWeight(nodeDetails.getWeight());
                    node.setNotifyCmd(nodeDetails.getNotifyCmd());
                    node.setNotifyValue(nodeDetails.getNotifyValue());
                    Node updatedNode = nodeJpaRepository.save(node);
                    return ResponseEntity.ok(updatedNode);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a node", responses = {
            @ApiResponse(description = "Node deleted successfully", responseCode = "200"),
            @ApiResponse(description = "Node not found", responseCode = "404")
    })
    @DeleteMapping("/{nodeId}")
    public ResponseEntity<?> deleteNode(@PathVariable String nodeId) {
        return nodeJpaRepository.findById(nodeId)
                .map(node -> {
                    nodeJpaRepository.delete(node);
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}