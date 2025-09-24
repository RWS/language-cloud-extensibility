using System;
using System.Threading;

namespace Rws.LC.AppBlueprint.Infrastructure
{
    // Simplified context helper to store tenant id in an async scope.
    public sealed class ContextScope : IDisposable
    {
        private readonly string _previous;
        public ContextScope(string tenantId)
        {
            _previous = Context.CurrentTenant;
            Context.CurrentTenant = tenantId;
        }

        public void Dispose()
        {
            Context.CurrentTenant = _previous;
        }
    }

    public static class Context
    {
        private static readonly AsyncLocal<string> _tenant = new AsyncLocal<string>();

        public static string CurrentTenant
        {
            get => _tenant.Value;
            set => _tenant.Value = value;
        }

        public static ContextScope BeginScope(string tenantId)
        {
            return new ContextScope(tenantId);
        }
    }
}
