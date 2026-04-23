package com.kolmir.subscription_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.subscription_service.dto.AddReactionRequest;
import com.kolmir.subscription_service.dto.DeleteReactionRequest;
import com.kolmir.subscription_service.dto.ReactionResponse;
import com.kolmir.subscription_service.service.ReactionService;

import static com.kolmir.subscription_service.util.ReactionUtil.*;

import java.util.Collection;
import java.util.Set;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping(POST_MAIN_URL)
public class ReactionController {
    private final ReactionService reactionService;

    @GetMapping(REACTIONS_URL)
    public ResponseEntity<Collection<ReactionResponse>> getReactionsForPosts(@RequestParam Set<Long> postIds) {
        return ResponseEntity.ok(reactionService.getReactionsForAllPosts(postIds));
    }

    @PatchMapping(REACTIONS_FOR_POST_URL)
    public ResponseEntity<ReactionResponse> addReaction(@RequestBody AddReactionRequest request) {
        return ResponseEntity.ok(reactionService.addReaction(request));
    }

    @DeleteMapping(REACTIONS_FOR_POST_URL)
    public ResponseEntity<Void> deleteReaction(@RequestBody DeleteReactionRequest request) {
        reactionService.deleteReaction(request);
        return ResponseEntity.noContent().build();
    }
}
