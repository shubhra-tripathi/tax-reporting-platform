
Feature: Posting validation

  Scenario: Accept a valid posting
    Given a posting with id "P001", account "A001" and amount 500
    When the posting is validated
    Then the posting should be valid


  Scenario: Reject a posting with negative amount
    Given a posting with id "P002", account "A001" and amount -100
    When the posting is validated
    Then the posting should be invalid