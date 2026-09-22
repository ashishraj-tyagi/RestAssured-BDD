@api @jsonplaceholder @users
Feature: User API (JSONPlaceholder legacy)
  As an API consumer
  I want to manage users
  So that I can verify user-related endpoints against JSONPlaceholder

  Background:
    Given the API is available

  @smoke
  Scenario: Retrieve all users
    When I fetch all users
    Then the response status code should be 200
    And the response should contain at least 1 users
    And the response body should match schema "schemas/users-list-schema.json"

  @smoke
  Scenario: Retrieve a single user by ID
    When I fetch user with id 1
    Then the response status code should be 200
    And the response field "id" should be "1"
    And the user email should be "Sincere@april.biz"
    And the response body should match schema "schemas/user-schema.json"

  Scenario: Create a new user
    When I create a user with name "Jane Doe" and email "jane.doe@example.com"
    Then the response status code should be 201
    And the response field "name" should be "Jane Doe"
    And the user email should be "jane.doe@example.com"
