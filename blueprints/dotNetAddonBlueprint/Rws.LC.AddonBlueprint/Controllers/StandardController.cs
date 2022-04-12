using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Rws.LC.AddonBlueprint.Enums;
using Rws.LC.AddonBlueprint.Helpers;
using Rws.LC.AddonBlueprint.Infrastructure;
using Rws.LC.AddonBlueprint.Interfaces;
using Rws.LC.AddonBlueprint.Models;
using Rws.LC.AddonBlueprint.Services;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.AddonBlueprint.Controllers
{
    [Route("v1")]
    [ApiController]
    public class StandardController : ControllerBase
    {
        /// <summary>
        /// The configuration
        /// </summary>
        private IConfiguration _configuration;

        /// <summary>
        /// The logger.
        /// </summary>
        private ILogger _logger;

        /// <summary>
        /// The descriptor service.
        /// </summary>
        private IDescriptorService _descriptorService;

        /// <summary>
        /// The account service.
        /// </summary>
        private IAccountService _accountService;

        /// <summary>
        /// The health reporter.
        /// </summary>
        private IHealthReporter _healthReporter;


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
        /// Gets the add-on descriptor.
        /// </summary>
        /// <returns>The descriptor</returns>
        [HttpGet("descriptor")]
        public IActionResult Descriptor()
        {
            // This endpoint provides the descriptor for the Language Cloud to inspect and register correctly.
            // It can be implemented in any number of ways. The example implementation is to load the descriptor.json file
            // into the AddonDescriptorModel object and then serialize it as a result.
            // Alternative implementation can be generating the descriptor based on config settings, environment variables,
            // etc.
            _logger.LogInformation("Entered Descriptor endpoint.");

            // Descriptor service will provide an object describing the descriptor.
            AddonDescriptorModel descriptor = _descriptorService.GetDescriptor();

            // TODO: You might need to change the baseUrl in appsettings.json
            descriptor.BaseUrl = _configuration["baseUrl"];

            // newtonsoft used to serialize entity with object type
            var jsonSettings = new Newtonsoft.Json.JsonSerializerSettings
            {
                NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore,
                ContractResolver = new Newtonsoft.Json.Serialization.CamelCasePropertyNamesContractResolver()
            };

            return Content(Newtonsoft.Json.JsonConvert.SerializeObject(descriptor, jsonSettings), "application/json", Encoding.UTF8);
        }

        /// <summary>
        /// Gets the add-on health.
        /// </summary>
        /// <returns>200 status code if it's healthy.</returns>
        [HttpGet("health")]
        public IActionResult Health()
        {
            // This is a health check endpoint. In most cases returnin Ok is enough, but you might want to make checks
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
        /// This endpoint provides the documentation for the Add-On. It can return the HTML page with the documentation
        /// or redirect to a page. In this sample redirect is used with URL configured in appsettings.json
        /// </summary>
        [HttpGet("documentation")]
        public IActionResult Documentation()
        {
            return Redirect(_configuration.GetValue<string>("documentationUrl"));
        }

        /// <summary>
        /// Receive lifecycle events for the Add-On.
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpPost("addon-lifecycle")]
        public async Task<IActionResult> AddonLifecycle()
        {
            string payload;
            using (StreamReader sr = new StreamReader(Request.Body))
            {
                payload = await sr.ReadToEndAsync();
            }

            var tenantId = HttpContext.User?.GetTenantId();

            var lifecycle = JsonSerializer.Deserialize<AddOnLifecycleEvent>(payload, JsonSettings.Default());
            switch (lifecycle.Id)
            {
                case AddOnLifecycleEventEnum.REGISTERED:
                    _logger.LogInformation($"Addon Registered in Language Cloud.");
                    // This is the event notifying that the Add-On has been registered in Language Cloud
                    // no further details are available for that event
                    break;
                case AddOnLifecycleEventEnum.ACTIVATED:
                    // This is an Activation event, tenant id and the client credentials must be saved to db.
                    AddOnLifecycleEvent<ActivatedEvent> activatedEvent = JsonSerializer.Deserialize<AddOnLifecycleEvent<ActivatedEvent>>(payload, JsonSettings.Default());

                    _logger.LogInformation($"Addon Activated Event Received for tenant id {tenantId}.");

                    await _accountService.SaveAccountInfo(activatedEvent.Data, tenantId, CancellationToken.None).ConfigureAwait(true);
                    break;
                case AddOnLifecycleEventEnum.UNREGISTERED:
                    // This is the event notifying that the Add-On has been unregistered/deleted from Language Cloud.
                    // No further details are available for that event.
                    _logger.LogInformation("Addon Unregistered Event Received.");
                    // All the tenant information should be removed.
                    await _accountService.RemoveAccounts(CancellationToken.None).ConfigureAwait(true);
                    break;

                case AddOnLifecycleEventEnum.DEACTIVATED:
                    // This is the event notifying that the Add-On has been uninstalled from a tenant account.
                    // No further details are available for that event.
                    _logger.LogInformation("Addon Deactivated Event Received.");
                    await _accountService.RemoveAccountInfo(tenantId, CancellationToken.None).ConfigureAwait(true);
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

            // newtonsoft used to serialize entity with dynamic type
            var resultValue = Content(JsonSerializer.Serialize(configurationSettingsResult, JsonSettings.Default()), "application/json", Encoding.UTF8);
            resultValue.StatusCode = 200;

            return resultValue;
        }

        /// <summary>
        /// Sets or updates the configuration settings.
        /// </summary>
        /// <returns>The updated configuration settings.</returns>
        [Authorize]
        [HttpPost("configuration")]
        public async Task<IActionResult> SetConfigurationSettings()
        {
            _logger.LogInformation("Setting the configuration settings.");

            // we deserialize this way to get the dynamic value from ConfigurationValueModel
            string payload;
            using (StreamReader sr = new StreamReader(Request.Body))
            {
                payload = await sr.ReadToEndAsync();
            }

            var tenantId = HttpContext.User?.GetTenantId();
            var configurationValues = Newtonsoft.Json.JsonConvert.DeserializeObject<List<ConfigurationValueModel>>(payload);

            // newtonsoft used to serialize entity with dynamic type
            ConfigurationSettingsResult configurationSettingsResult = await _accountService.SaveOrUpdateConfigurationSettings(tenantId, configurationValues, CancellationToken.None).ConfigureAwait(true);
            var resultValue = Content(JsonSerializer.Serialize(configurationSettingsResult, JsonSettings.Default()), "application/json", Encoding.UTF8);
            resultValue.StatusCode = 200;

            return resultValue;
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
    }
}