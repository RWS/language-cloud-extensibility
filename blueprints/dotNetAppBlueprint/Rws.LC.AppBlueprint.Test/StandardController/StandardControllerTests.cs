using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using NSubstitute;
using Rws.LC.AppBlueprint.Helpers;
using Rws.LC.AppBlueprint.Interfaces;
using Rws.LC.AppBlueprint.Models;
using Rws.LC.AppBlueprint.Test.Helpers;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Text.Json.Nodes;
using System.Threading.Tasks;
using Xunit;

namespace Rws.LC.AppBlueprint.Test.StandardController
{
    [TestCaseOrderer("Rws.LC.AppBlueprint.Test.Helpers.PriorityOrderer", "Rws.LC.AppBlueprint.Test")]
    public class StandardControllerTests
    {
        readonly IConfiguration _configuration;
        readonly IDescriptorService _descriptorService;
        readonly IAccountService _accountService;
        readonly IHealthReporter _healthReporter;

        readonly static MockTenant _mockTenant = new MockTenant();

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
        }

        [Fact, TestPriority(0)]
        public async Task AppLifecycleUnregisterForCleanup()
        {
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("UnregisteredEventRequest.json", stream));

                try
                {
                    await standardController.AppLifecycle().ConfigureAwait(false);
                }
                catch
                {
                    // ignore any errors
                }
            }
        }


        [Fact, TestPriority(1)]
        public void GetDescriptor()
        {
            var standardController = BuildStandardController(new DefaultHttpContext());

            var result = standardController.Descriptor();
            var descriptor = (result as OkObjectResult).Value as JsonNode;

            Assert.Equal(_configuration["baseUrl"], descriptor["baseUrl"].ToString());
            Assert.Equal("1.0.0", descriptor["version"].ToString());
            Assert.Equal("1.4", descriptor["descriptorVersion"].ToString());
            Assert.Equal(2, descriptor["extensions"].AsArray().Count);
            Assert.Equal("/v1/health", descriptor["standardEndpoints"]["health"].ToString());
            Assert.Equal("/v1/documentation", descriptor["standardEndpoints"]["documentation"].ToString());
            Assert.Equal("/v1/app-lifecycle", descriptor["standardEndpoints"]["appLifecycle"].ToString());
            Assert.Equal("/v1/configuration", descriptor["standardEndpoints"]["configuration"].ToString());
            Assert.Equal("/v1/configuration/validation", descriptor["standardEndpoints"]["configurationValidation"].ToString());
            Assert.Equal("/v1/privacyPolicy", descriptor["standardEndpoints"]["privacyPolicy"].ToString());
            Assert.Equal("/v1/termsAndConditions", descriptor["standardEndpoints"]["termsAndConditions"].ToString());
            Assert.Single(descriptor["configurations"].AsArray());
        }

        [Fact, TestPriority(2)]
        public void HealthCheck()
        {
            var standardController = BuildStandardController(new DefaultHttpContext());

            var response = standardController.Health();

            Assert.True(response is OkResult);
        }

        [Fact, TestPriority(3)]
        public void GetDocumentation()
        {
            var standardController = BuildStandardController(new DefaultHttpContext());

            var response = standardController.Documentation();

            var result = response as RedirectResult;
            Assert.Equal(_configuration["documentationUrl"], result.Url);
        }

        [Fact, TestPriority(4)]
        public async Task AppLifecycleRegister()
        {
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("RegisteredEventRequest.json", stream));

                var response = await standardController.AppLifecycle().ConfigureAwait(false);

                Assert.True(response is OkResult);
            }
        }

        [Fact, TestPriority(5)]
        public async Task AppLifecycleInstall()
        {
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("InstalledEventRequest.json", stream));

                var response = await standardController.AppLifecycle().ConfigureAwait(false);

                Assert.True(response is OkResult);
            }
        }

        [Fact, TestPriority(6)]
        public async Task SetConfigurationSettings()
        {
            // first install the app
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("InstalledEventRequest.json", stream));
                await standardController.AppLifecycle().ConfigureAwait(false);
            }
            // then set the configuration settings on the account install entity
            using (var stream = new MemoryStream())
            {
                var httpContext = new DefaultHttpContext();
                httpContext.User = _mockTenant.GetDefaultPrincipal();
                var standardController = BuildStandardController(httpContext);

                var configSettingsRequest = JsonSerializer.Deserialize<List<ConfigurationValueModel>>(File.ReadAllText("TestFiles\\ConfigurationRequest.json"), JsonSettings.Default());

                var response = await standardController.SetConfigurationSettings(configSettingsRequest).ConfigureAwait(false);

                var configurationSettings = (response as OkObjectResult).Value as ConfigurationSettingsResult;
                Assert.Equal(1, configurationSettings.ItemCount);
                Assert.Single(configurationSettings.Items);
                Assert.Equal("SAMPLE_CONFIG_ID", configurationSettings.Items.First().Id);
                Assert.Equal("sampleConfigValue", configurationSettings.Items.First().Value.ToString());
            }
        }

        [Fact, TestPriority(7)]
        public async Task ValidateConfigurationSettings()
        {
            // first install the app
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("InstalledEventRequest.json", stream));
                await standardController.AppLifecycle().ConfigureAwait(false);
            }
            // then set the configuration settings on the account install entity
            using (var stream = new MemoryStream())
            {
                var httpContext = new DefaultHttpContext();
                httpContext.User = _mockTenant.GetDefaultPrincipal();
                var standardController = BuildStandardController(httpContext);

                var configSettingsRequest = JsonSerializer.Deserialize<List<ConfigurationValueModel>>(File.ReadAllText("TestFiles\\ConfigurationRequest.json"), JsonSettings.Default());

                await standardController.SetConfigurationSettings(configSettingsRequest).ConfigureAwait(false);
            }
            // prepare context user for the config validation request
            var httpContext2 = new DefaultHttpContext();
            httpContext2.User = _mockTenant.GetDefaultPrincipal();
            var testedStandardController = BuildStandardController(httpContext2);

            var response = await testedStandardController.ValidateConfiguration().ConfigureAwait(false);

            Assert.True(response is OkResult);
        }

        [Fact, TestPriority(8)]
        public async Task GetConfigurationSettings()
        {
            // first install the app
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("InstalledEventRequest.json", stream));
                await standardController.AppLifecycle().ConfigureAwait(false);
            }
            // then set the configuration settings on the account install entity
            using (var stream = new MemoryStream())
            {
                var httpContext = new DefaultHttpContext();
                httpContext.User = _mockTenant.GetDefaultPrincipal();
                var standardController = BuildStandardController(httpContext);

                var configSettingsRequest = JsonSerializer.Deserialize<List<ConfigurationValueModel>>(File.ReadAllText("TestFiles\\ConfigurationRequest.json"), JsonSettings.Default());

                await standardController.SetConfigurationSettings(configSettingsRequest).ConfigureAwait(false);
            }
            // prepare context user for the GET config settings request
            var httpContext2 = new DefaultHttpContext();
            httpContext2.User = _mockTenant.GetDefaultPrincipal();
            var testedStandardController = BuildStandardController(httpContext2);

            var response = await testedStandardController.GetConfigurationSettings().ConfigureAwait(false);

            var configurationSettings = (response as OkObjectResult).Value as ConfigurationSettingsResult;
            Assert.Equal(1, configurationSettings.ItemCount);
            Assert.Single(configurationSettings.Items);
            Assert.Equal("SAMPLE_CONFIG_ID", configurationSettings.Items.First().Id);
            Assert.Equal("sampleConfigValue", configurationSettings.Items.First().Value.ToString());
        }

        [Fact, TestPriority(9)]
        public async Task AppLifecycleUninstall()
        {
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("UninstalledEventRequest.json", stream));

                var response = await standardController.AppLifecycle().ConfigureAwait(false);

                Assert.True(response is OkResult);
            }
        }

        [Fact, TestPriority(10)]
        public async Task AppLifecycleUnregister()
        {
            using (var stream = new MemoryStream())
            {
                var standardController = BuildStandardController(BuildRequestContext("UnregisteredEventRequest.json", stream));

                var response = await standardController.AppLifecycle().ConfigureAwait(false);

                Assert.True(response is OkResult);
            }
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

            httpContext.Request.Headers.TryAdd(Constants.AppIdHeader, "AppId");
            httpContext.Request.Headers.TryAdd(Constants.DevTenantIdHeader, _mockTenant.FakeAccountId);

            return httpContext;
        }
    }
}
