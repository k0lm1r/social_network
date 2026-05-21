Feature: Feed service business behavior

  Scenario: User feed is built from followings and uses expected sorting
    Given a user with followings and posts in feed service
    When the user requests their feed
    Then the feed contains only posts from followings
    And feed query uses createdAt and popularity descending sorting

  Scenario: Popularity is recalculated from likes dislikes and comments
    Given an existing post with reactions and comments
    When post popularity is recalculated
    Then the post popularity is saved with expected value

  Scenario: Created post is assigned to current user
    Given current user is configured in feed service
    When the user creates a post in feed service
    Then the created post author matches current user

  Scenario: Post ownership check reflects current user
    Given current user is configured in feed service
    And post ownership in repository is true
    When feed service checks post ownership
    Then post ownership check returns true

  Scenario: Comment ownership check reflects current user
    Given current user is configured for comment service
    And comment ownership in repository is true
    When feed service checks comment ownership
    Then comment ownership check returns true

  Scenario: Missing post returns not found for get update and delete
    Given post is missing in feed service
    When feed service loads updates and deletes missing post
    Then post not found error is raised in each operation

  Scenario: Missing comment returns not found for get and delete
    Given comment is missing in feed service
    When comment service loads and deletes missing comment
    Then comment not found error is raised in each operation

  Scenario: Post write operations are configured to evict caches
    When cache eviction annotations are inspected on post service write methods
    Then each write method has cache eviction configuration
