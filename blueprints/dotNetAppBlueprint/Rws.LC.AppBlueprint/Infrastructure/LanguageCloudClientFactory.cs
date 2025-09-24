using System;
using System.Collections.Concurrent;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace Rws.LC.AppBlueprint.Infrastructure
{
    // Minimal factory that returns a RegionClientContainerFactory placeholder per region.
    public class LanguageCloudClientFactory
    {
        private readonly IServiceProvider _serviceProvider;
        private readonly IConfiguration _configuration;
        private readonly ConcurrentDictionary<string, RegionClientContainerFactory> _cache = new();

        public LanguageCloudClientFactory(IServiceProvider serviceProvider, IConfiguration configuration)
        {
            _serviceProvider = serviceProvider;
            _configuration = configuration;
        }

        public RegionClientContainerFactory Region(string region)
        {
            if (string.IsNullOrEmpty(region)) region = "global";
            return _cache.GetOrAdd(region, r => new RegionClientContainerFactory(_serviceProvider, _configuration, r));
        }
    }
}
