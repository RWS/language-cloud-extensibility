using System.Net.Http;
using System.Threading;
using Rws.LC.VerificationSampleApp.Interfaces;
using Rws.LC.VerificationSampleApp.Models;
using System.Threading.Tasks;
using System.Collections.Generic;
using Rws.LC.VerificationSampleApp.Exceptions;
using System;
using Rws.LC.AddonSample.Preview.Helpers;
using Rws.LC.VerificationSampleApp.Exceptions;

namespace Rws.LC.VerificationSampleApp.Services
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


