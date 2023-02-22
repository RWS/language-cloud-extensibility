using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using NSubstitute;
using Rws.LC.AddonBlueprint.Helpers;
using Rws.LC.AddonBlueprint.Interfaces;
using Rws.LC.AddonBlueprint.Models;
using Rws.LC.AddonBlueprint.Test.Helpers;
using System;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Text.Json.Nodes;
using System.Threading.Tasks;
using Xunit;

namespace Rws.LC.AddonBlueprint.Test.StandardController
{
    public class StandardControllerTests
    {
        readonly IConfiguration _configuration;
        readonly IDescriptorService _descriptorService;
        readonly IAccountService _accountService;
        readonly IHealthReporter _healthReporter;

        readonly MockTenant _mockTenant;

        public StandardControllerTests()
        {
            IConfiguration configuration = new ConfigurationBuilder()
                .AddJsonFile("appsettings.test.json")
                 //.AddEnvironmentVariables()
                 .Build();

            var startup = new Startup(configuration);
            var services = new ServiceCollection();

            startup.ConfigureServices(services);
            services.AddSingleton<IConfiguration>(configuration);

            var serviceProvider = services.BuildServiceProvider();
            _configuration = serviceProvider.GetService<IConfiguration>();
            _descriptorService = serviceProvider.GetService<IDescriptorService>();
            _accountService = serviceProvider.GetService<IAccountService>();
            _healthReporter = serviceProvider.GetService<IHealthReporter>();

            _mockTenant = new MockTenant();
        }


        [Fact]
        public void GetDescriptor()
        {
            var standardController = BuildStandardController(new DefaultHttpContext());

            var result = standardController.Descriptor();
            var descriptor = (result as OkObjectResult).Value as JsonNode;

            Assert.Equal(_configuration["baseUrl"], descriptor["baseUrl"].ToString());
            Assert.Equal("1.0.0", descriptor["version"].ToString());
            Assert.Equal("1.3", descriptor["descriptorVersion"].ToString());
            Assert.Equal(2, descriptor["extensions"].AsArray().Count);
            Assert.Equal("/v1/health", descriptor["standardEndpoints"]["health"].ToString());
            Assert.Equal("/v1/documentation", descriptor["standardEndpoints"]["documentation"].ToString());
            Assert.Equal("/v1/addon-lifecycle", descriptor["standardEndpoints"]["addonLifecycle"].ToString());
            Assert.Equal("/v1/configuration", descriptor["standardEndpoints"]["configuration"].ToString());
            Assert.Equal("/v1/configuration/validation", descriptor["standardEndpoints"]["configurationValidation"].ToString());
            Assert.Equal("/v1/privacyPolicy", descriptor["standardEndpoints"]["privacyPolicy"].ToString());
            Assert.Equal("/v1/termsAndConditions", descriptor["standardEndpoints"]["termsAndConditions"].ToString());
            Assert.Single(descriptor["configurations"].AsArray());
        }

        [Fact]
        public void HealthCheck()
        {
            var standardController = BuildStandardController(new DefaultHttpContext());

            var response = standardController.Health();

            Assert.True(response is OkResult);
        }

        [Fact]
        public void GetDocumentation()
        {
            var standardController = BuildStandardController(new DefaultHttpContext());

            var response = standardController.Documentation();

            var result = response as RedirectResult;
            Assert.Equal(_configuration["documentationUrl"], result.Url);
        }

        [Fact]
        public async Task AddonLifecycleRegister()
        {
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("RegisteredEventRequest.json", stream));

                var response = await standardController.AddonLifecycle().ConfigureAwait(false);

                Assert.True(response is OkResult);
            }
        }

        [Fact]
        public async Task AddonLifecycleUnregister()
        {
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("UnregisteredEventRequest.json", stream));

                var response = await standardController.AddonLifecycle().ConfigureAwait(false);

                Assert.True(response is OkResult);
            }
        }

        [Fact]
        public async Task AddonLifecycleActivate()
        {
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("ActivatedEventRequest.json", stream));

                var response = await standardController.AddonLifecycle().ConfigureAwait(false);

                Assert.True(response is OkResult);
            }
        }

        [Fact]
        public async Task AddonLifecycleDeactivate()
        {
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("DeactivatedEventRequest.json", stream));

                var response = await standardController.AddonLifecycle().ConfigureAwait(false);

                Assert.True(response is OkResult);
            }
        }

        [Fact]
        public async Task SetConfigurationSettings()
        {
            // first activate the add-on
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("ActivatedEventRequest.json", stream));
                await standardController.AddonLifecycle().ConfigureAwait(false);
            }
            // then set the configuration settings on the account activation entity
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("ConfigurationRequest.json", stream));

                var response = await standardController.SetConfigurationSettings().ConfigureAwait(false);

                var configurationSettings = JsonSerializer.Deserialize<ConfigurationSettingsResult>((response as ContentResult).Content, JsonSettings.Default());
                Assert.Equal(1, configurationSettings.ItemCount);
                Assert.Single(configurationSettings.Items);
                Assert.Equal("SAMPLE_CONFIG_ID", configurationSettings.Items.First().Id);
                Assert.Equal("sampleConfigValue", Convert.ToString(configurationSettings.Items.First().Value));
            }
        }

        [Fact]
        public async Task GetConfigurationSettings()
        {
            // first activate the add-on
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("ActivatedEventRequest.json", stream));
                await standardController.AddonLifecycle().ConfigureAwait(false);
            }
            // then set the configuration settings on the account activation entity
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("ConfigurationRequest.json", stream));
                await standardController.SetConfigurationSettings().ConfigureAwait(false);
            }
            // prepare context user for the GET config settings request
            var httpContext = new DefaultHttpContext();
            httpContext.User = _mockTenant.GetDefaultPrincipal();
            var testedStandardController = BuildStandardController(httpContext);

            var response = await testedStandardController.GetConfigurationSettings().ConfigureAwait(false);

            var configurationSettings = JsonSerializer.Deserialize<ConfigurationSettingsResult>((response as ContentResult).Content, JsonSettings.Default());
            Assert.Equal(1, configurationSettings.ItemCount);
            Assert.Single(configurationSettings.Items);
            Assert.Equal("SAMPLE_CONFIG_ID", configurationSettings.Items.First().Id);
            Assert.Equal("sampleConfigValue", Convert.ToString(configurationSettings.Items.First().Value));
        }

        [Fact]
        public async Task ValidateConfigurationSettings()
        {
            // first activate the add-on
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("ActivatedEventRequest.json", stream));
                await standardController.AddonLifecycle().ConfigureAwait(false);
            }
            // then set the configuration settings on the account activation entity
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("ConfigurationRequest.json", stream));
                await standardController.SetConfigurationSettings().ConfigureAwait(false);
            }
            // prepare context user for the config validation request
            var httpContext = new DefaultHttpContext();
            httpContext.User = _mockTenant.GetDefaultPrincipal();
            var testedStandardController = BuildStandardController(httpContext);

            var response = await testedStandardController.ValidateConfiguration().ConfigureAwait(false);

            Assert.True(response is OkResult);
        }

        private Controllers.StandardController BuildStandardController(HttpContext httpContext)
        {
            var standardControllerLogger = Substitute.For<ILogger<Controllers.StandardController>>();

            var controller = new Controllers.StandardController(_configuration, standardControllerLogger, _descriptorService, _accountService, _healthReporter)
            {
                ControllerContext = new ControllerContext
                {
                    HttpContext = httpContext
                }
            };

            return controller;
        }

        private HttpContext BuildRequestContext(string requestFilePath, MemoryStream stream)
        {
            var httpContext = new DefaultHttpContext();

            httpContext.User = _mockTenant.GetDefaultPrincipal();

            var requestPayload = File.ReadAllText($"TestFiles\\{requestFilePath}");
            var payloadBytes = Encoding.UTF8.GetBytes(requestPayload);
            stream.Write(payloadBytes, 0, payloadBytes.Length);
            httpContext.Request.Body = stream;
            httpContext.Request.Body.Position = 0;
            httpContext.Request.ContentLength = stream.Length;

            return httpContext;
        }
    }
}
