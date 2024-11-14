using System;
using System.Collections.Generic;
using System.Security.Claims;

namespace Rws.LC.AppBlueprint.Test.Helpers
{
    public class MockTenant
    {
        public string FakeAccountId { get; private set; }

        public MockTenant()
        {
            FakeAccountId = Guid.NewGuid().ToString();
        }

        public ClaimsPrincipal GetDefaultPrincipal()
        {
            List<Claim> _claims = new List<Claim>();

            var defaultClaims = new[]
            {
                new Claim("aid", FakeAccountId)
            };

            _claims.AddRange(defaultClaims);

            return CreatePrincipalWithClaims(_claims);
        }

        private static ClaimsPrincipal CreatePrincipalWithClaims(IEnumerable<Claim> claims)
        {
            var identity = new ClaimsIdentity();
            identity.AddClaims(claims);

            var principal = new ClaimsPrincipal();
            principal.AddIdentity(identity);

            return principal;
        }
    }
}
