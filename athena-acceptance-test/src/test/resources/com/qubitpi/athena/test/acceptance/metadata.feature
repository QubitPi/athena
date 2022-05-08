Feature: MetaData can be read via GraphQL GET and POST endpoints as specified by GraphQL HEEP spec

  Scenario: MetaData gets returned
    Given the query is asking for file name only
    When the query is executed
    Then response contains only one field, which is file name

  Scenario:
    Given the query is asking for multiple metadata fields
    When the query is executed
    Then response contains all requested fields
