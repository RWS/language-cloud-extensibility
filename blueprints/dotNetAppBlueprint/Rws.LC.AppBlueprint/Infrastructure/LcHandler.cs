using System;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Caching.Memory;
using Rws.LanguageCloud.Sdk.Authentication;
using Sdl.ApiClientSdk.Core;
using Rws.LC.AppBlueprint.DAL;
using Rws.LC.AppBlueprint.Infrastructure;
using Rws.LC.AppBlueprint.Interfaces;

namespace Rws.LC.AppBlueprint.Infrastructure
{
    /// <summary>
    /// Example of a custom LC authentication handler.
    /// Must be registered as transient servicer in the DI container.
    /// </summary>
    public class LcHandler : LCCustomAuthenticationHandler
    {
        private readonly IHttpContextAccessor context;
        private readonly ILogger<LcHandler> logger;
        private readonly IRepository repository;

        private readonly IAppRegistrationRepository appRegistrationRepository;
        private readonly IMemoryCache memoryCache;

        public LcHandler(IHttpContextAccessor httpContext, ILogger<LcHandler> logger, IRepository repository, IAppRegistrationRepository appRegistrationRepository, IMemoryCache memoryCache) : base(logger)  
        {
            context = httpContext;
            this.logger = logger;
            this.repository = repository;
            this.appRegistrationRepository = appRegistrationRepository;
            this.memoryCache = memoryCache;
        }

        /// <summary>
        /// Example of a <see cref="ServiceCredentials"/> provider.
        /// </summary>
        protected override ServiceCredentials GetServiceCredentials()
        {
            string tenant = ApiClientContext.GetValue(Context.TenantName) as string;
            if (string.IsNullOrEmpty(tenant))
            {
                tenant = context.HttpContext.User?.GetTenantId();

                if (string.IsNullOrEmpty(tenant))
                {
                    logger.LogError("Tenant could not be found in current HTTP Context or scoped context.");
                    throw new ArgumentNullException(nameof(tenant), "Tenant could not be found in current HTTP Context or scoped context.");
                }
            }
            // get the credentials from the repository
            var registrationInfo = memoryCache.GetOrCreate("RegistrationInfo", entry =>
            {
                entry.AbsoluteExpirationRelativeToNow = TimeSpan.FromSeconds(60);
                return appRegistrationRepository.GetRegistrationInfo().Result;
            });
            var credentials = new ServiceCredentials(registrationInfo.ClientCredentials.ClientId,
                registrationInfo.ClientCredentials.ClientSecret, tenant);
            return credentials;
        }

        /// <summary>
        /// Example of a Trace Id provider.
        /// </summary>
        protected override string GetTraceId()
        {
            return DateTimeOffset.UtcNow.ToString();
        }
    }
}
