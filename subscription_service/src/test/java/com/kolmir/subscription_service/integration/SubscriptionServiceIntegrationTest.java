package com.kolmir.subscription_service.integration;

import static com.kolmir.subscription_service.testutil.integration.SubscriptionIntegrationTestConstants.*;
import static com.kolmir.subscription_service.testutil.integration.SubscriptionIntegrationTestObjectFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.mockito.Mockito;

import com.kolmir.security.provider.CurrentUserProvider;
import com.kolmir.subscription_service.dto.subscription.SubscriptionLinkResponse;
import com.kolmir.subscription_service.openfeign.service.PostService;
import com.kolmir.subscription_service.openfeign.service.UserExistenceService;
import com.kolmir.subscription_service.repository.InteractionEventRepository;
import com.kolmir.subscription_service.repository.ReactionRepository;
import com.kolmir.subscription_service.repository.SubscriptionLinkRepository;
import com.kolmir.subscription_service.service.ReactionService;
import com.kolmir.subscription_service.service.SubscriptionLinkService;

@SpringBootTest(
    properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
    }
)
@Testcontainers
class SubscriptionServiceIntegrationTest {

    @Container
    static final MongoDBContainer mongo = new MongoDBContainer(MONGO_IMAGE);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private SubscriptionLinkService subscriptionLinkService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private SubscriptionLinkRepository subscriptionLinkRepository;

    @Autowired
    private InteractionEventRepository interactionEventRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private UserExistenceService userExistenceService;

    @Autowired
    private PostService postService;

    @BeforeEach
    void setUp() {
        reactionRepository.deleteAll();
        interactionEventRepository.deleteAll();
        subscriptionLinkRepository.deleteAll();

        when(currentUserProvider.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        doNothing().when(userExistenceService).validateUserExists(org.mockito.ArgumentMatchers.anyLong());
        when(postService.isPostExists(org.mockito.ArgumentMatchers.anyLong())).thenReturn(true);
        doNothing().when(postService).updatePopularity(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void followAndUnfollow_persistAndRemoveSubscriptionInMongo() {
        SubscriptionLinkResponse created = subscriptionLinkService.follow(TARGET_USER_ID);

        assertThat(created.followerId()).isEqualTo(CURRENT_USER_ID);
        assertThat(created.followingId()).isEqualTo(TARGET_USER_ID);
        assertThat(subscriptionLinkRepository.existsByFollowerIdAndFollowingId(CURRENT_USER_ID, TARGET_USER_ID)).isTrue();

        subscriptionLinkService.unfollow(TARGET_USER_ID);

        assertThat(subscriptionLinkRepository.existsByFollowerIdAndFollowingId(CURRENT_USER_ID, TARGET_USER_ID)).isFalse();
    }

    @Test
    void addAndDeleteReaction_updatesCountersInMongo() {
        var postId = MAIN_POST_ID;

        var addResponse = reactionService.addReaction(addLikeReactionRequest(), postId);
        assertThat(addResponse.postId()).isEqualTo(postId);
        assertThat(addResponse.likeCount()).isEqualTo(1);
        assertThat(addResponse.dislikeCount()).isEqualTo(0);

        var allReactions = reactionService.getReactionsForAllPosts(Set.of(postId, SECOND_POST_ID));
        assertThat(allReactions).hasSize(EXPECTED_REACTIONS_SIZE);

        var deleteResponse = reactionService.deleteReaction(postId);
        assertThat(deleteResponse.likeCount()).isEqualTo(0);
        assertThat(deleteResponse.dislikeCount()).isEqualTo(0);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        CurrentUserProvider currentUserProvider() {
            return Mockito.mock(CurrentUserProvider.class);
        }

        @Bean
        @Primary
        UserExistenceService userExistenceService() {
            return Mockito.mock(UserExistenceService.class);
        }

        @Bean
        @Primary
        PostService postService() {
            return Mockito.mock(PostService.class);
        }
    }
}
