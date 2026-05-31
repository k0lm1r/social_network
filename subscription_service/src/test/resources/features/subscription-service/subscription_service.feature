Feature: Subscription service business behavior

  Scenario: User can follow existing target user
    Given current user is configured in subscription service
    And target user exists in identity service
    When the user follows another target user
    Then subscription link is created

  Scenario: User cannot follow themselves
    Given current user is configured in subscription service
    When the user follows themselves
    Then follow operation fails with business error

  Scenario: Existing subscription can be removed
    Given current user is configured in subscription service
    And subscription link exists for current user and target
    When the user unfollows target user
    Then subscription link is deleted

  Scenario: Missing subscription cannot be removed
    Given current user is configured in subscription service
    And subscription link does not exist for current user and target
    When the user unfollows target user
    Then unfollow operation fails with not found error

  Scenario: Followers and followings counters and lists are returned
    Given target user exists in identity service
    And repository has followers and followings data
    When user requests followers and followings data
    Then followers and followings responses are correct

  Scenario: New reaction replaces previous user reaction and updates counters
    Given current user is configured in subscription service
    And reaction counters for main post are likes 0 and dislikes 1
    And the user already has dislike reaction for main post
    When the user sets like reaction for main post
    Then reaction counters become likes 1 and dislikes 0
    And popularity update is triggered twice for main post

  Scenario: Existing reaction can be deleted
    Given current user is configured in subscription service
    And reaction counters for main post are likes 1 and dislikes 0
    And the user already has like reaction for main post
    When the user deletes reaction for main post
    Then reaction counters become likes 0 and dislikes 0

  Scenario: Reaction list includes zero counters for missing posts
    Given reaction repository has data only for main post
    When requesting reaction counters for main and second post
    Then responses include second post with zero counters

  Scenario: Duplicate interaction events are rejected
    Given interaction event for like already exists
    When creating duplicate like interaction event
    Then interaction event creation fails with already exists error

  Scenario: Identity service degradation is mapped to external service error
    Given identity service is unavailable
    When validating user existence through user existence service
    Then external service error is thrown

  Scenario: Feed service not found on popularity update is mapped to not found
    Given feed service returns 404 for popularity update
    When updating popularity through post service facade
    Then post not found error is thrown
