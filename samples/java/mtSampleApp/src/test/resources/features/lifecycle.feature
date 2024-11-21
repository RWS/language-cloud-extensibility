Feature:  lifecycle

  Scenario: I unregister the app
    When I send an app lifecycle event with id "UNREGISTERED"
    Then Last response status code is 200

  Scenario: I register the app
    When I register the app
    Then Last response status code is 200

  Scenario: I send an illegal app lifecycle event type
    When I send an app lifecycle event with id "ILLEGAL"
    Then Last response status code is 422
    And Last error code is "validationException"

  Scenario: I activate the app
    When I uninstall the app
    When I install the app
    Then Last response status code is 200
    When I uninstall the app
    Then Last response status code is 200

  Scenario: I can activate an account that has been activated before
    When I uninstall the app
    When I install the app
    Then Last response status code is 200
    When I install the app
    Then Last response status code is 200
#    After I deactivate the app I can reactivate it
    When I uninstall the app
    When I install the app
    Then Last response status code is 200
    When I uninstall the app