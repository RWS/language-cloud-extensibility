using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Rws.LC.AppBlueprint.Enums;
using Rws.LC.AppBlueprint.Helpers;
using Rws.LC.AppBlueprint.Infrastructure;
using Rws.LC.AppBlueprint.Interfaces;
using Rws.LC.AppBlueprint.Models;
using System.Collections.Generic;
using System.IO;
using System.Text.Json;
using System.Text.Json.Nodes;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.AppBlueprint.Controllers
{
    [Route("v1")]
    [ApiController]
    public class StandardController : ControllerBase
    {
        /// <summary>
        /// The configuration
        /// </summary>
        private readonly IConfiguration _configuration;

        /// <summary>
        /// The logger.
        /// </summary>
        private readonly ILogger _logger;

        /// <summary>
        /// The descriptor service.
        /// </summary>
        private readonly IDescriptorService _descriptorService;

        /// <summary>
        /// The account service.
        /// </summary>
        private readonly IAccountService _accountService;

        /// <summary>
        /// The health reporter.
        /// </summary>
        private readonly IHealthReporter _healthReporter;


        /// <summary>
        /// Initializes a new instance of the <see cref="StandardController"/> class.
        /// </summary>
        /// <param name="configuration">The configuration.</param>
        /// <param name="logger">The logger.</param>
        /// <param name="descriptorService">The descriptor service.</param>
        /// <param name="accountService">The account service.</param>
        /// <param name="healthReporter">The health reporter.</param>
        public StandardController(IConfiguration configuration,
            ILogger<StandardController> logger,
            IDescriptorService descriptorService,
            IAccountService accountService,
            IHealthReporter healthReporter)
        {
            _configuration = configuration;
            _logger = logger;
            _descriptorService = descriptorService;
            _accountService = accountService;
            _healthReporter = healthReporter;
        }

        /// <summary>
        /// Gets the app descriptor.
        /// </summary>
        /// <returns>The descriptor</returns>
        [HttpGet("descriptor")]
        public IActionResult Descriptor()
        {
            // This endpoint provides the descriptor for the Language Cloud to inspect and register correctly.
            // It can be implemented in any number of ways. The example implementation is to load the descriptor.json file
            // Alternative implementation can be generating the descriptor based on config settings, environment variables,
            // etc.
            _logger.LogInformation("Entered Descriptor endpoint.");

            // Descriptor service will provide an object describing the descriptor.
            JsonNode descriptor = _descriptorService.GetDescriptor();

            // TODO: You might need to change the baseUrl in appsettings.json
            descriptor["baseUrl"] = _configuration["baseUrl"];

            return Ok(descriptor);
        }

        /// <summary>
        /// Gets the app health.
        /// </summary>
        /// <returns>200 status code if it's healthy.</returns>
        [HttpGet("health")]
        public IActionResult Health()
        {
            // This is a health check endpoint. In most cases returning Ok is enough, but you might want to make checks
            // to resources this service uses, like: DB, message queues, storage etc.
            // Any response besides 200 Ok, will be considered as failure. As a suggestion use "return StatusCode(500);"
            // when you need to signal that the service is having health issues.

            var isHealthy = _healthReporter.IsServiceHealthy();
            if (isHealthy)
            {
                return Ok();
            }

            return StatusCode(500);
        }

        /// <summary>
        /// This endpoint provides the documentation for the app. It can return the HTML page with the documentation
        /// or redirect to a page. In this sample redirect is used with URL configured in appsettings.json
        /// </summary>
        [HttpGet("documentation")]
        public IActionResult Documentation()
        {
            return Redirect(_configuration.GetValue<string>("documentationUrl"));
        }

        /// <summary>
        /// Receive lifecycle events for the app.
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpPost("addon-lifecycle")]
        [HttpPost("app-lifecycle")]
        public async Task<IActionResult> AppLifecycle()
        {
            string payload;
            using (StreamReader sr = new StreamReader(Request.Body))
            {
                payload = await sr.ReadToEndAsync();
            }

            var tenantId = HttpContext.User?.GetTenantId();

            var lifecycle = JsonSerializer.Deserialize<AppLifecycleEvent>(payload, JsonSettings.Default());
            switch (lifecycle.Id)
            {
                case AppLifecycleEventEnum.REGISTERED:
                    _logger.LogInformation($"App Registered in Language Cloud.");
                    // This is the event notifying that the App has been registered in Language Cloud
                    // no further details are available for that event
                    AppLifecycleEvent<RegisteredUpdatedEvent> registeredEvent = JsonSerializer.Deserialize<AppLifecycleEvent<RegisteredUpdatedEvent>>(payload, JsonSettings.Default());
                    await _accountService.SaveRegistrationInfo(registeredEvent.Data, CancellationToken.None).ConfigureAwait(true);
                    break;
                case AppLifecycleEventEnum.UPDATED:
                    _logger.LogDebug($"App Updated Event Received for tenant id {tenantId}.");
                    AppLifecycleEvent<RegisteredUpdatedEvent> updatedEvent = JsonSerializer.Deserialize<AppLifecycleEvent<RegisteredUpdatedEvent>>(payload, JsonSettings.Default());
                    await _accountService.UpdateToDescriptorVersion14(updatedEvent.Data, CancellationToken.None).ConfigureAwait(true);
                    break;
                case AppLifecycleEventEnum.ACTIVATED:
                case AppLifecycleEventEnum.INSTALLED:
                    _logger.LogInformation("App Installed Event Received for tenant id {TenantId}.", tenantId);

                    await _accountService.SaveAccountInfo(tenantId, CancellationToken.None).ConfigureAwait(true);
                    break;
                case AppLifecycleEventEnum.DEACTIVATED:
                case AppLifecycleEventEnum.UNINSTALLED:
                    // This is the event notifying that the App has been uninstalled from a tenant account.
                    // No further details are available for that event.
                    _logger.LogInformation("App Uninstalled Event Received.");
                    await _accountService.RemoveAccountInfo(tenantId, CancellationToken.None).ConfigureAwait(true);
                    break;
                case AppLifecycleEventEnum.UNREGISTERED:
                    // This is the event notifying that the App has been unregistered/deleted from Language Cloud.
                    // No further details are available for that event.
                    _logger.LogInformation("App Unregistered Event Received.");
                    // All the tenant information should be removed.
                    await _accountService.RemoveAccounts(CancellationToken.None).ConfigureAwait(true);
                    // Remove the registration information
                    await _accountService.RemoveRegistrationInfo(CancellationToken.None).ConfigureAwait(true);
                    break;
            }

            return Ok();
        }

        /// <summary>
        /// Gets the configuration settings.
        /// </summary>
        /// <returns>The updated configuration settings.</returns>
        [Authorize]
        [HttpGet("configuration")]
        public async Task<IActionResult> GetConfigurationSettings()
        {
            // All configuration settings must be returned to the caller.
            // Configurations that are secret will be returned with the value set to "*****", if they have a value.

            _logger.LogInformation("Retrieving the configuration settings.");
            var tenantId = HttpContext.User?.GetTenantId();
            ConfigurationSettingsResult configurationSettingsResult = await _accountService.GetConfigurationSettings(tenantId, CancellationToken.None).ConfigureAwait(true);

            return Ok(configurationSettingsResult);
        }

        /// <summary>
        /// Sets or updates the configuration settings.
        /// </summary>
        /// <returns>The updated configuration settings.</returns>
        [Authorize]
        [HttpPost("configuration")]
        public async Task<IActionResult> SetConfigurationSettings(List<ConfigurationValueModel> configurationValues)
        {
            _logger.LogInformation("Setting the configuration settings.");

            var tenantId = HttpContext.User?.GetTenantId();

            ConfigurationSettingsResult configurationSettingsResult = await _accountService.SaveOrUpdateConfigurationSettings(tenantId, configurationValues, CancellationToken.None).ConfigureAwait(true);

            return Ok(configurationSettingsResult);
        }

        /// <summary>
        /// Validates the configuration.
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpPost("configuration/validation")]
        public async Task<IActionResult> ValidateConfiguration()
        {

            _logger.LogInformation("Validating the configuration settings.");

            var tenantId = HttpContext.User?.GetTenantId();
            await _accountService.ValidateConfigurationSettings(tenantId, CancellationToken.None).ConfigureAwait(true);

            return Ok();
        }

        /// <summary>
        /// This endpoint provides the privacy policy for the app. It can return the HTML page with the privacy policy
        /// or redirect to a page. In this sample redirect is used the static file privacyPolicy.html.
        /// </summary>
        [HttpGet("privacyPolicy")]
        public IActionResult PrivacyPolicy()
        {
            var html = System.IO.File.ReadAllText(@"./resources/privacyPolicy.html");
            return base.Content(html, "text/html");
        }

        /// <summary>
        /// This endpoint provides the terms and conditions for the app. It can return the HTML page with the terms and conditions
        /// or redirect to a page. In this sample redirect is used the static file termsAndCondition.html.
        /// </summary>
        [HttpGet("termsAndConditions")]
        public IActionResult TermsANdConditions()
        {
            var html = System.IO.File.ReadAllText(@"./resources/termsAndConditions.html");
            return base.Content(html, "text/html");
        }
    }
}