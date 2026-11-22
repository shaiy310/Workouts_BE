package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.model.ExerciseWeight;
import com.YaakoviCorp.Fit1337.model.WeightUpdateRequest;
import com.YaakoviCorp.Fit1337.security.model.AuthenticatedUser;
import com.YaakoviCorp.Fit1337.service.ExerciseWeightService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/weights")
public class ExerciseWeightController {
    private static final Logger log = LoggerFactory.getLogger(ExerciseWeightController.class);
    private final ExerciseWeightService weightService;

    public ExerciseWeightController(ExerciseWeightService weightService) {
        this.weightService = weightService;
    }

    // GET /api/weights
    @GetMapping
    public ResponseEntity<List<ExerciseWeight>> getWeights(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<ExerciseWeight> weights = weightService.getUserWeights(currentUser.id());
        log.debug("controller=exercise_weights_read outcome=success userId={} resultCount={}",
                currentUser.id(), weights.size());
        return ResponseEntity.ok(weights);
    }

    // POST /api/weights/{exerciseName}
    @PostMapping("/{exerciseName}")
    public ResponseEntity<ExerciseWeight> updateWeight(
            @PathVariable @NotBlank @Size(max = 200) String exerciseName,
            @RequestBody @Valid WeightUpdateRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        ExerciseWeight updated = weightService.updateWeight(currentUser.id(), exerciseName, request.weight());
        log.info("controller=exercise_weight_update outcome=success userId={} exerciseName={}",
                currentUser.id(), exerciseName.trim());
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/weights/{exerciseName}
    @DeleteMapping("/{exerciseName}")
    public ResponseEntity<Void> deleteWeight(
            @PathVariable @NotBlank @Size(max = 200) String exerciseName,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        weightService.deleteWeight(currentUser.id(), exerciseName);
        log.info("controller=exercise_weight_delete outcome=success userId={} exerciseName={}",
                currentUser.id(), exerciseName.trim());
        return ResponseEntity.noContent().build();
    }
}
