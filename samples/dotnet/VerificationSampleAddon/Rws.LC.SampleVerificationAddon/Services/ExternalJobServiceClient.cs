using System.Net.Http;
using System.Threading;
using Rws.LC.SampleVerificationAddon.RestService.Interfaces;
using Rws.LC.SampleVerificationAddon.RestService.Models;
using System.Threading.Tasks;
using System.Collections.Generic;
using Rws.LC.SampleVerificationAddon.RestService.Exceptions;
using System;
using Rws.LC.AddonSample.Preview.Helpers;
using Rws.LC.SampleVerificationAddon.Exceptions;

namespace Rws.LC.SampleVerificationAddon.RestService.Services
{
    public class ExternalJobServiceClient : RestClientBase, IExternalJobServiceClient
    {
        public async Task SendCallback(string callbackUrl, VerificationResponse verificationResponse, string tenantId,
            CancellationToken cancellationToken)
        {
            var headers = new Dictionary<string, string> { { "X-LC-Tenant", tenantId } };
            try
            {
                await SendAsync(callbackUrl, HttpMethod.Post, verificationResponse, headers, cancellationToken)
                    .ConfigureAwait(false);
            }
            catch(Exception ex)
            {
                throw new SendCallbackException("Error when sending callback", new Details { Code = "Verification.Job.Error.Callback", Name="Verification Job", Value = ex.Message });
            }
        }
    }
}


