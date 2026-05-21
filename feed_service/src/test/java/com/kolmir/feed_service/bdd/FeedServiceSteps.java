package com.kolmir.feed_service.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.kolmir.feed_service.dto.post.PostRequest;
import com.kolmir.feed_service.dto.post.PostResponse;
import com.kolmir.feed_service.exception.NotFoundException;
import com.kolmir.feed_service.mapper.CommentMapper;
import com.kolmir.feed_service.mapper.PostMapper;
import com.kolmir.feed_service.model.Post;
import com.kolmir.feed_service.openfeign.dto.ReactionResponse;
import com.kolmir.feed_service.openfeign.service.FollowingAndReactionsService;
import com.kolmir.feed_service.repository.CommentRepository;
import com.kolmir.feed_service.repository.PostRepository;
import com.kolmir.feed_service.service.CommentService;
import com.kolmir.feed_service.service.impl.CommentServiceImpl;
import com.kolmir.feed_service.service.impl.PostServiceImpl;
import com.kolmir.feed_service.testutil.bdd.FeedBddObjectFactory;
import com.kolmir.feed_service.testutil.bdd.FeedBddTestConstants;
import com.kolmir.security.provider.CurrentUserProvider;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class FeedServiceSteps {
    private final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private final PostMapper postMapper = Mockito.mock(PostMapper.class);
    private final CommentService commentService = Mockito.mock(CommentService.class);
    private final CurrentUserProvider currentUserProvider = Mockito.mock(CurrentUserProvider.class);
    private final FollowingAndReactionsService followingAndReactionsService = Mockito.mock(FollowingAndReactionsService.class);

    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final CommentMapper commentMapper = Mockito.mock(CommentMapper.class);

    private final PostServiceImpl postService = new PostServiceImpl(
        postRepository,
        postMapper,
        commentService,
        currentUserProvider,
        followingAndReactionsService
    );

    private final CommentServiceImpl commentServiceImpl = new CommentServiceImpl(
        commentMapper,
        commentRepository,
        currentUserProvider
    );

    private final Pageable pageable = PageRequest.of(FeedBddTestConstants.PAGE_NUMBER, FeedBddTestConstants.PAGE_SIZE);

    private Page<PostResponse> feedPage;
    private PostResponse createdPostResponse;
    private Boolean postOwnership;
    private Boolean commentOwnership;
    private Throwable getPostError;
    private Throwable updatePostError;
    private Throwable deletePostError;
    private Throwable getCommentError;
    private Throwable deleteCommentError;
    private Post popularityPost;

    private Method createMethod;
    private Method updateMethod;
    private Method deleteMethod;
    private Method updatePopularityMethod;

    @Given("a user with followings and posts in feed service")
    public void aUserWithFollowingsAndPostsInFeedService() {
        when(followingAndReactionsService.getFollowingsIdsForUser(FeedBddTestConstants.USER_ID))
            .thenReturn(List.of(FeedBddTestConstants.FIRST_AUTHOR_ID, FeedBddTestConstants.SECOND_AUTHOR_ID));

        Post post1 = FeedBddObjectFactory.post(1L, FeedBddTestConstants.FIRST_AUTHOR_ID, "first");
        Post post2 = FeedBddObjectFactory.post(2L, FeedBddTestConstants.SECOND_AUTHOR_ID, "second");
        Page<Post> page = new PageImpl<>(List.of(post1, post2), pageable, 2);

        when(postRepository.findByAuthorIdIn(any(), any(Pageable.class))).thenReturn(page);
        when(postMapper.toResponse(any(Post.class))).thenAnswer(invocation ->
            FeedBddObjectFactory.postResponse(invocation.getArgument(0))
        );
    }

    @When("the user requests their feed")
    public void theUserRequestsTheirFeed() {
        feedPage = postService.getFeedForUser(FeedBddTestConstants.USER_ID, pageable);
    }

    @Then("the feed contains only posts from followings")
    public void theFeedContainsOnlyPostsFromFollowings() {
        List<Long> authorIds = feedPage.getContent().stream().map(PostResponse::authorId).toList();
        assertEquals(List.of(FeedBddTestConstants.FIRST_AUTHOR_ID, FeedBddTestConstants.SECOND_AUTHOR_ID), authorIds);
    }

    @Then("feed query uses createdAt and popularity descending sorting")
    public void feedQueryUsesCreatedAtAndPopularityDescendingSorting() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(postRepository).findByAuthorIdIn(any(), pageableCaptor.capture());
        Sort sort = pageableCaptor.getValue().getSort();
        List<Sort.Order> orders = sort.toList();

        assertEquals("createdAt", orders.get(0).getProperty());
        assertEquals(Sort.Direction.DESC, orders.get(0).getDirection());
        assertEquals("popularity", orders.get(1).getProperty());
        assertEquals(Sort.Direction.DESC, orders.get(1).getDirection());
    }

    @Given("an existing post with reactions and comments")
    public void anExistingPostWithReactionsAndComments() {
        popularityPost = FeedBddObjectFactory.post(FeedBddTestConstants.POST_ID, FeedBddTestConstants.OWNER_ID, "popularity");
        when(postRepository.findById(FeedBddTestConstants.POST_ID)).thenReturn(Optional.of(popularityPost));
        when(followingAndReactionsService.getReactionsForPost(FeedBddTestConstants.POST_ID))
            .thenReturn(new ReactionResponse(
                FeedBddTestConstants.POST_ID,
                FeedBddTestConstants.LIKES,
                FeedBddTestConstants.DISLIKES
            ));
        when(commentService.getCommentsCountForPost(FeedBddTestConstants.POST_ID)).thenReturn(FeedBddTestConstants.COMMENTS);
    }

    @When("post popularity is recalculated")
    public void postPopularityIsRecalculated() {
        postService.updatePopularity(FeedBddTestConstants.POST_ID);
    }

    @Then("the post popularity is saved with expected value")
    public void thePostPopularityIsSavedWithExpectedValue() {
        assertEquals(FeedBddTestConstants.POPULARITY, popularityPost.getPopularity());
        verify(postRepository).save(popularityPost);
    }

    @Given("current user is configured in feed service")
    public void currentUserIsConfiguredInFeedService() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(FeedBddTestConstants.OWNER_ID);

        when(postMapper.toPost(any(PostRequest.class))).thenAnswer(invocation -> {
            PostRequest request = invocation.getArgument(0);
            return FeedBddObjectFactory.post(null, null, request.text());
        });

        when(postMapper.toResponse(any(Post.class))).thenAnswer(invocation ->
            FeedBddObjectFactory.postResponse(invocation.getArgument(0))
        );

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(99L);
            return post;
        });
    }

    @When("the user creates a post in feed service")
    public void theUserCreatesAPostInFeedService() {
        createdPostResponse = postService.create(new PostRequest(FeedBddTestConstants.POST_TEXT));
    }

    @Then("the created post author matches current user")
    public void theCreatedPostAuthorMatchesCurrentUser() {
        assertEquals(FeedBddTestConstants.OWNER_ID, createdPostResponse.authorId());
    }

    @Given("post ownership in repository is true")
    public void postOwnershipInRepositoryIsTrue() {
        when(postRepository.existsByIdAndAuthorId(FeedBddTestConstants.POST_ID, FeedBddTestConstants.OWNER_ID)).thenReturn(true);
    }

    @When("feed service checks post ownership")
    public void feedServiceChecksPostOwnership() {
        postOwnership = postService.isCurrentUserOwner(FeedBddTestConstants.POST_ID);
    }

    @Then("post ownership check returns true")
    public void postOwnershipCheckReturnsTrue() {
        assertEquals(true, postOwnership);
    }

    @Given("current user is configured for comment service")
    public void currentUserIsConfiguredForCommentService() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(FeedBddTestConstants.OWNER_ID);
    }

    @Given("comment ownership in repository is true")
    public void commentOwnershipInRepositoryIsTrue() {
        when(commentRepository.existsByIdAndAuthorId(FeedBddTestConstants.COMMENT_ID, FeedBddTestConstants.OWNER_ID)).thenReturn(true);
    }

    @When("feed service checks comment ownership")
    public void feedServiceChecksCommentOwnership() {
        commentOwnership = commentServiceImpl.isCurrentUserOwner(FeedBddTestConstants.COMMENT_ID);
    }

    @Then("comment ownership check returns true")
    public void commentOwnershipCheckReturnsTrue() {
        assertEquals(true, commentOwnership);
    }

    @Given("post is missing in feed service")
    public void postIsMissingInFeedService() {
        when(postRepository.findById(FeedBddTestConstants.MISSING_POST_ID)).thenReturn(Optional.empty());
    }

    @When("feed service loads updates and deletes missing post")
    public void feedServiceLoadsUpdatesAndDeletesMissingPost() {
        getPostError = assertThrows(NotFoundException.class, () -> postService.getById(FeedBddTestConstants.MISSING_POST_ID));
        updatePostError = assertThrows(NotFoundException.class,
            () -> postService.update(FeedBddTestConstants.MISSING_POST_ID, new PostRequest(FeedBddTestConstants.UPDATED_POST_TEXT)));
        deletePostError = assertThrows(NotFoundException.class, () -> postService.delete(FeedBddTestConstants.MISSING_POST_ID));
    }

    @Then("post not found error is raised in each operation")
    public void postNotFoundErrorIsRaisedInEachOperation() {
        assertEquals(NotFoundException.class, getPostError.getClass());
        assertEquals(NotFoundException.class, updatePostError.getClass());
        assertEquals(NotFoundException.class, deletePostError.getClass());
    }

    @Given("comment is missing in feed service")
    public void commentIsMissingInFeedService() {
        when(commentRepository.findById(FeedBddTestConstants.MISSING_COMMENT_ID)).thenReturn(Optional.empty());
    }

    @When("comment service loads and deletes missing comment")
    public void commentServiceLoadsAndDeletesMissingComment() {
        getCommentError = assertThrows(NotFoundException.class, () -> commentServiceImpl.getById(FeedBddTestConstants.MISSING_COMMENT_ID));
        deleteCommentError = assertThrows(NotFoundException.class, () -> commentServiceImpl.delete(FeedBddTestConstants.MISSING_COMMENT_ID));
    }

    @Then("comment not found error is raised in each operation")
    public void commentNotFoundErrorIsRaisedInEachOperation() {
        assertEquals(NotFoundException.class, getCommentError.getClass());
        assertEquals(NotFoundException.class, deleteCommentError.getClass());
    }

    @When("cache eviction annotations are inspected on post service write methods")
    public void cacheEvictionAnnotationsAreInspectedOnPostServiceWriteMethods() throws Exception {
        createMethod = PostServiceImpl.class.getMethod("create", PostRequest.class);
        updateMethod = PostServiceImpl.class.getMethod("update", Long.class, PostRequest.class);
        deleteMethod = PostServiceImpl.class.getMethod("delete", Long.class);
        updatePopularityMethod = PostServiceImpl.class.getMethod("updatePopularity", Long.class);
    }

    @Then("each write method has cache eviction configuration")
    public void eachWriteMethodHasCacheEvictionConfiguration() {
        assertNotNull(createMethod.getAnnotation(CacheEvict.class));
        assertNotNull(updateMethod.getAnnotation(Caching.class));
        assertNotNull(deleteMethod.getAnnotation(Caching.class));
        assertNotNull(updatePopularityMethod.getAnnotation(Caching.class));
    }
}
