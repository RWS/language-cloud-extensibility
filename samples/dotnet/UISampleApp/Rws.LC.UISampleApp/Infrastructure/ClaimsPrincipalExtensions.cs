using System.Security.Claims;

namespace Rws.LC.UISampleApp.Infrastructure
{
    public static class ClaimsPrincipalExtensions
    {
        public static string GetTenantId(this ClaimsPrincipal principal)
        {
            return principal.FindFirstValue("aid");
        }
    }
}
