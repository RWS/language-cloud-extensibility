Feature:  manage

  Scenario: Get App descriptor
    When I get the app descriptor
    Then Last response status code is 200
    And The descriptor name should be "Blueprint App"
    And The descriptor version is "1.0.0"
    And The descriptor has 2 extensions

  Scenario: Get App health
    When I get the app health
    Then Last response status code is 200
    And The app health response is true

  Scenario: Get App documentation
    When I get the app documentation
    Then Last response status code is 302



