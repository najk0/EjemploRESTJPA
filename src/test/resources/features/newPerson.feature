@person
Feature: Create a new person in the Agenda
  In order to create a new person
  As a user
  I want to create a new Person

  Scenario: Create a new person
    Given I am on the main page
    And No user with nif "123" exists
    When I provide "Oscar" for the name
    And I provide "Belmonte" for the surname
    And I provide "123" for the nif
    And I click the New button
    Then The person with nif "123" is created in the Agenda