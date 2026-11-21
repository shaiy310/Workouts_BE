package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.model.ActivityEvent;
import com.YaakoviCorp.Fit1337.security.model.AuthenticatedUser;
import com.YaakoviCorp.Fit1337.service.ActivityEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/event")
public class ActivityEventController {

    private final ActivityEventService eventService;

    public ActivityEventController(ActivityEventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/event
    // GET /api/event?start=2026-11-01&end=2026-11-20
    @GetMapping
    public ResponseEntity<List<ActivityEvent>> getEvents(
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(eventService.getUserEvents(currentUser.id(), start, end));
    }

    // POST /api/event
    @PostMapping
    public ResponseEntity<ActivityEvent> createEvent(
            @RequestBody @Valid ActivityEvent event,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return eventService.recordEvent(event, currentUser.id())
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}