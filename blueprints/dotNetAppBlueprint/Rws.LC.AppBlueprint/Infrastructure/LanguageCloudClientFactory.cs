using System;
using System.Collections.Concurrent;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace Rws.LC.AppBlueprint.Infrastructure
{
    public class LanguageCloudClientFactory
    {
        private object _lock = new object();
        private readonly IServiceProvider _serviceProvider;
        private readonly IConfiguration _configuration;

        ConcurrentDictionary<string, RegionClientContainerFactory> _regionClientContainerFactories = new ConcurrentDictionary<string, RegionClientContainerFactory>();

        public LanguageCloudClientFactory(IServiceProvider serviceProvider, IConfiguration configuration)
        {
            _serviceProvider = serviceProvider;
            _configuration = configuration;
        }

        public RegionClientContainerFactory Region(string region)
        {
            if (!_regionClientContainerFactories.TryGetValue(region, out RegionClientContainerFactory regionClientContainerFactory))
            {
                lock (_lock)
                {
                    if (!_regionClientContainerFactories.TryGetValue(region, out regionClientContainerFactory))
                    {
                        regionClientContainerFactory = new RegionClientContainerFactory(region, _serviceProvider, _configuration);
                        _regionClientContainerFactories.TryAdd(region, regionClientContainerFactory);
                    }
                }
            }

            return regionClientContainerFactory;
        }
    }
}
