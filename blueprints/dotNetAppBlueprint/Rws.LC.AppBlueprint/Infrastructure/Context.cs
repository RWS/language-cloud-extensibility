using System;
using System.Collections.Generic;
using System.Threading;
using Amazon.Runtime;
using Sdl.ApiClientSdk.Core;

namespace Rws.LC.AppBlueprint.Infrastructure
{
    /// <summary>
    /// Stores contextual information (e.g., Tenant) for the current async flow.
    /// </summary>
    public static class Context
    {
        public static readonly string TenantName = "tenant";

        public static IDisposable BeginScope(string tenant)
        {
            var contextValues = new Dictionary<string, object>
            {
                { TenantName, tenant }
            };
            return ApiClientContext.BeginScope(contextValues);
        }
    }
}
