Feature:  manage

  Scenario: Get Addon descriptor
    When I get the add-on descriptor
    Then Last response status code is 200
    And The descriptor name should be "Blueprint Addon"
    And The descriptor version is "1.0.0"
    And The descriptor has 2 extensions

  Scenario: Get addon health
    When I get the add-on health
    Then Last response status code is 200
    And The add-on health response is true

  Scenario: Get addon documentation
    When I get the add-on documentation
    Then Last response status code is 302



