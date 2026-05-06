package com.kolmir.subscription_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.subscription_service.dto.event.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.event.InteractionEventResponse;
import com.kolmir.subscription_service.service.InteractionEventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.kolmir.subscription_service.util.InteractionEventUtil.*;
import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.ID_URL;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping(EVENT_MAIN_URL)
public class InteractionEventController {
    private final InteractionEventService service;
    
    @GetMapping
    public ResponseEntity<List<InteractionEventResponse>> getAllByAction(@RequestParam(required = false) String action) {
        List<InteractionEventResponse> responses;
        if (action == null || action.isBlank())
            responses = service.getAll();
        else 
            responses = service.getAllByAction(action);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(ID_URL)
    public ResponseEntity<InteractionEventResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getEventById(id));
    }    
    
    @PostMapping
    public ResponseEntity<InteractionEventResponse> createEvent(@Valid @RequestBody CreateInteractionEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }
    
    @DeleteMapping(ID_URL)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
