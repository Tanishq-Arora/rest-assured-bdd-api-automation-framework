Feature: Task Updates API

# As an API consumer
# I want to manage task updates
# So that I can create, retrieve, update and delete task records

  Background:
    Given the task updates collection is available


  @create
  Scenario: Create a new task update
    Given I have a valid task update request
    When I create the task update
    Then the response status code should be 201
    And the task update should be created successfully

  @create
  Scenario: Create a pending task update
    Given I have a pending task update request
    When I create the task update
    Then the response status code should be 201
    And the task update should be created successfully
    And the task update should be pending


  @retrieve
  Scenario: Retrieve task updates
    When I retrieve all task updates
    Then the response status code should be 200
    And the task updates response should contain records


  @retrieve
  Scenario: Retrieve a task update by ID
    Given a task update exists
    When I retrieve the task update by its ID
    Then the response status code should be 200
    And the task update details should be returned

  @retrieve @negative
  Scenario: Retrieve a task update using an invalid ID
    When I retrieve a task update using an invalid ID
    Then the response status code should be 404


  @update
  Scenario: Update an existing task update
    Given a task update exists
    And I have updated task details
    When I update the task update
    Then the response status code should be 200
    And the task update should contain the updated details

  @update @negative
  Scenario: Update a task update using an invalid ID
    Given I have a valid task update request
    When I update a task update using an invalid ID
    Then the response status code should be 404


  @patch
  Scenario: Partially update an existing task update
    Given a task update exists
    And I have partially updated task details
    When I patch the task update
    Then the response status code should be 200
    And the task update should contain the patched details

  @delete
  Scenario: Delete an existing task update
    Given a task update exists
    When I delete the task update
    Then the response status code should be 204

  @delete @negative
  Scenario: Delete a task update using an invalid ID
    When I delete a task update using an invalid ID
    Then the response status code should be 404