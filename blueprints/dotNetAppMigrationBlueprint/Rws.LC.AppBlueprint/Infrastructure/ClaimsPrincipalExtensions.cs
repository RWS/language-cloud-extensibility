﻿using System.Security.Claims;

namespace Rws.LC.AppBlueprint.Infrastructure
{
    public static class ClaimsPrincipalExtensions
    {
        public static string GetTenantId(this ClaimsPrincipal principal)
        {
            return principal.FindFirstValue("aid");
        }
    }
}
