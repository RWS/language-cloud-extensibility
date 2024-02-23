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

  Scenario: I install the app
    When I uninstall the app
    When I install the app
    Then Last response status code is 200
    When I uninstall the app
    Then Last response status code is 200

  Scenario: I can install an account that has been installed before
    When I uninstall the app
    When I install the app
    Then Last response status code is 200
    When I install the app
    Then Last response status code is 200
#    After I uninstall the app I can reinstall it
    When I uninstall the app
    When I install the app
    Then Last response status code is 200
    When I uninstall the app