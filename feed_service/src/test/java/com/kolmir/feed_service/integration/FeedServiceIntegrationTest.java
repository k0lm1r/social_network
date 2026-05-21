package com.kolmir.feed_service.integration;

import static com.kolmir.feed_service.testutil.integration.FeedIntegrationTestConstants.*;
import static com.kolmir.feed_service.testutil.integration.FeedIntegrationTestObjectFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kolmir.feed_service.openfeign.service.FollowingAndReactionsService;
import com.kolmir.feed_service.repository.CommentRepository;
import com.kolmir.feed_service.repository.PostRepository;
import com.kolmir.feed_service.service.PostService;
import com.kolmir.security.provider.CurrentUserProvider;
import org.mockito.Mockito;

@SpringBootTest(
    properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.liquibase.enabled=true",
        "spring.cache.type=redis"
    }
)
@Testcontainers
class FeedServiceIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
        .withDatabaseName(FEED_TEST_DB)
        .withUsername(DB_USERNAME)
        .withPassword(DB_PASSWORD);

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>(REDIS_IMAGE)
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private FollowingAndReactionsService followingAndReactionsService;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();

        when(currentUserProvider.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(followingAndReactionsService.getFollowingsIdsForUser(CURRENT_USER_ID))
            .thenReturn(List.of(CURRENT_USER_ID, FOLLOWING_AUTHOR_ID));
        when(followingAndReactionsService.getReactionsForPost(org.mockito.ArgumentMatchers.anyLong()))
            .thenReturn(reactionResponse(0L, REACTION_LIKES, REACTION_DISLIKES));
    }

    @Test
    void persistsPostAndCommentInPostgres() {
        var createdPost = postRepository.save(post(CURRENT_USER_ID, INTEGRATION_POST_TEXT, ZERO_POPULARITY));
        var createdComment = commentRepository.save(comment(createdPost, CURRENT_USER_ID, INTEGRATION_COMMENT_TEXT));

        assertThat(createdPost.getId()).isNotNull();
        assertThat(createdPost.getAuthorId()).isEqualTo(CURRENT_USER_ID);
        assertThat(createdComment.getId()).isNotNull();
        assertThat(createdComment.getAuthorId()).isEqualTo(CURRENT_USER_ID);

        var loadedPost = postRepository.findById(createdPost.getId()).orElseThrow();
        assertThat(loadedPost.getPopularity()).isEqualTo(ZERO_POPULARITY);
        assertThat(commentRepository.findAllByPostId(createdPost.getId(), PageRequest.of(PAGE_INDEX, PAGE_SIZE)).getTotalElements())
            .isEqualTo(1);
    }

    @Test
    void getFeedForUser_returnsOnlyFollowingsPostsWithExpectedSorting() {
        savePost(FOLLOWING_AUTHOR_ID, FOLLOWING_OLD_TEXT, ZERO_POPULARITY);
        savePost(FOLLOWING_AUTHOR_ID, FOLLOWING_NEW_TEXT, MEDIUM_POPULARITY);
        savePost(OUTSIDER_AUTHOR_ID, OUTSIDER_TEXT, HIGH_POPULARITY);

        when(followingAndReactionsService.getFollowingsIdsForUser(CURRENT_USER_ID))
            .thenReturn(List.of(FOLLOWING_AUTHOR_ID));

        var page = postService.getFeedForUser(
            CURRENT_USER_ID,
            PageRequest.of(PAGE_INDEX, PAGE_SIZE, Sort.unsorted())
        );

        assertThat(page.getContent()).hasSize(EXPECTED_FEED_SIZE);
        assertThat(page.getContent())
            .extracting(p -> p.authorId())
            .containsOnly(FOLLOWING_AUTHOR_ID);
    }

    private void savePost(Long authorId, String text, Double popularity) {
        postRepository.save(post(authorId, text, popularity));
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
        FollowingAndReactionsService followingAndReactionsService() {
            return Mockito.mock(FollowingAndReactionsService.class);
        }
    }
}
