package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.model.ExerciseWeight;
import com.YaakoviCorp.Fit1337.model.WeightUpdateRequest;
import com.YaakoviCorp.Fit1337.security.model.AuthenticatedUser;
import com.YaakoviCorp.Fit1337.service.ExerciseWeightService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weights")
public class ExerciseWeightController {
    private final ExerciseWeightService weightService;

    public ExerciseWeightController(ExerciseWeightService weightService) {
        this.weightService = weightService;
    }

    // GET /api/weights
    @GetMapping
    public ResponseEntity<List<ExerciseWeight>> getWeights(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(weightService.getUserWeights(currentUser.id()));
    }

    // POST /api/weights/{exerciseName}
    @PostMapping("/{exerciseName}")
    public ResponseEntity<ExerciseWeight> updateWeight(
            @PathVariable @NotBlank @Size(max = 200) String exerciseName,
            @RequestBody @Valid WeightUpdateRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        ExerciseWeight updated = weightService.updateWeight(currentUser.id(), exerciseName, request.weight());
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/weights/{exerciseName}
    @DeleteMapping("/{exerciseName}")
    public ResponseEntity<Void> deleteWeight(
            @PathVariable @NotBlank @Size(max = 200) String exerciseName,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        weightService.deleteWeight(currentUser.id(), exerciseName);
        return ResponseEntity.noContent().build();
    }
}
