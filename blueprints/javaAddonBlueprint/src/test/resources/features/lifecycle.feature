Feature:  lifecycle

  Scenario: I unregister the addon
    When I send an addon lifecycle event with id "UNREGISTERED"
    Then Last response status code is 200

  Scenario: I register the addon
    When I send an addon lifecycle event with id "REGISTERED"
    Then Last response status code is 200

  Scenario: I send an illegal addon lifecycle event type
    When I send an addon lifecycle event with id "ILLEGAL"
    Then Last response status code is 422
    And Last error code is "validationException"

  Scenario: I activate the addon
    When I deactivate the addon
    When I activate the addon
    Then Last response status code is 200
    When I deactivate the addon
    Then Last response status code is 200

  Scenario: I can only activate an account that hasn't been activated before
    When I deactivate the addon
    When I activate the addon
    Then Last response status code is 200
    When I activate the addon
    Then Last response status code is 422
    And Last error message contains "Account already activated"
#    After I deactivate the addon I can reactivate it
    When I deactivate the addon
    When I activate the addon
    Then Last response status code is 200
    When I deactivate the addon