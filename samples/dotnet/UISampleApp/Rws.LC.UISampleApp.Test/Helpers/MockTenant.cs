using System;
using System.Collections.Generic;
using System.Security.Claims;

namespace Rws.LC.UISampleApp.Test.Helpers
{
    public class MockTenant
    {
        private readonly string _fakeAccountId;

        public MockTenant()
        {
            _fakeAccountId = Guid.NewGuid().ToString();
        }

        public ClaimsPrincipal GetDefaultPrincipal()
        {
            List<Claim> _claims = new List<Claim>();

            var defaultClaims = new[]
            {
                new Claim("aid", _fakeAccountId)
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
