@api @jsonplaceholder @posts
Feature: Posts API (JSONPlaceholder legacy)
  As an API consumer
  I want to interact with posts
  So that I can verify CRUD operations against JSONPlaceholder

  @smoke
  Scenario: Get posts for a user
    When I send a GET request to "/posts" with query parameters:
      | parameter | value |
      | userId    | 1     |
    Then the response status code should be 200
    And the response list should contain at least 1 items

  Scenario: Create a post
    When I send a POST request to "/posts" with body:
      """
      {
        "title": "BDD with Cucumber",
        "body": "Automating APIs with RestAssured",
        "userId": 1
      }
      """
    Then the response status code should be 201
    And the response field "title" should be "BDD with Cucumber"
    And the response field "id" should not be empty
