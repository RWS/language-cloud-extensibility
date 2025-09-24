using System;
using System.Collections.Concurrent;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Rws.LanguageCloud.Sdk;
using Rws.LanguageCloud.Sdk.Authentication;
using Rws.LC.AppBlueprint.Services;

namespace Rws.LC.AppBlueprint.Infrastructure
{
    public class RegionClientContainerFactory
    {
        LanguageCloudClientProvider _languageCloudClientProvider;

        private readonly IServiceProvider _serviceProvider;
        private readonly IConfiguration _configuration;

        private IAccountClient _accountClient;
        private IProjectClient _projectClient;

        public RegionClientContainerFactory(string region, IServiceProvider serviceProvider, IConfiguration configuration)
        {
            _configuration = configuration;
            _serviceProvider = serviceProvider;
            
            _languageCloudClientProvider = new LanguageCloudClientProvider(region);
            if (_configuration.GetValue<string>("PublicApiUrl") != null)
                LanguageCloudClientProvider.GlobalBaseUrl = _configuration.GetValue<string>("PublicApiUrl");

            // Override the default Auth0 endpoint if specified in the configuration
            if (_configuration.GetValue<string>("Auth0:Audience") != null)
                Auth0Provider.Instance.Audience = _configuration.GetValue<string>("Auth0:Audience");

            if (_configuration.GetValue<string>("Auth0:AuthorizationEndpoint") != null)
                Auth0Provider.Instance.AuthorizationEndpoint = _configuration.GetValue<string>("Auth0:AuthorizationEndpoint");

            // get custom authentication handler
            LcHandler handler = _serviceProvider.GetRequiredService<LcHandler>();
            LanguageCloudClientProvider.DefaultHandler = handler;
        }

        // TODO: Next we have examples of cached clients, you can add more if needed

        public IAccountClient AccountClient
        {
            get
            {
                if (_accountClient == null)
                {
                    _accountClient = _languageCloudClientProvider.GetAccountClientNoAuth();
                }
                return _accountClient;
            }
        }

        public IProjectClient ProjectClient
        {
            get
            {
                if (_projectClient == null)
                {
                    _projectClient = _languageCloudClientProvider.GetProjectClientNoAuth();
                }
                return _projectClient;
            }
        }
    }
}
