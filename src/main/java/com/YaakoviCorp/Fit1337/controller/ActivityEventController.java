package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.model.ActivityEvent;
import com.YaakoviCorp.Fit1337.security.model.AuthenticatedUser;
import com.YaakoviCorp.Fit1337.service.ActivityEventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/events")
public class ActivityEventController {
    private static final Logger log = LoggerFactory.getLogger(ActivityEventController.class);

    private final ActivityEventService eventService;

    public ActivityEventController(ActivityEventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/events
    // GET /api/events?start=2026-11-01&end=2026-11-20
    @GetMapping
    public ResponseEntity<List<ActivityEvent>> getEvents(
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<ActivityEvent> events = eventService.getUserEvents(currentUser.id(), start, end);
        log.debug("controller=activity_events_read outcome=success userId={} resultCount={}",
                currentUser.id(), events.size());
        return ResponseEntity.ok(events);
    }

    // POST /api/events
    @PostMapping
    public ResponseEntity<ActivityEvent> createEvent(
            @RequestBody @Valid ActivityEvent event,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        var recorded = eventService.recordEvent(event, currentUser.id());
        if (recorded.isPresent()) {
            log.info("controller=activity_event_create outcome=success userId={} eventType={}",
                    currentUser.id(), event.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.CREATED).body(recorded.get());
        }

        log.info("controller=activity_event_create outcome=success status=no_content userId={} eventType={}",
                currentUser.id(), event.getClass().getSimpleName());
        return ResponseEntity.noContent().build();
    }
}