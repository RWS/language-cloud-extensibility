using Rws.LC.AddonSample.Preview.Helpers;
using Rws.LC.VerificationSampleApp.Exceptions;
using Rws.LC.VerificationSampleApp.Exceptions;
using Rws.LC.VerificationSampleApp.Interfaces;
using Rws.LC.VerificationSampleApp.Models;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Services
{
    public class VerificationServiceClient : RestClientBase, IVerificationServiceClient
    {
        public async Task PublishVerificationMessages(string publishMessageEndpoint, VerificationMessageResponse verificationResponse, string requestId, string traceId, CancellationToken cancellationToken)
        {
            var headers = new Dictionary<string, string> { { "TR_ID", traceId} };
            
            try
            {
               
                await SendAsync(publishMessageEndpoint, HttpMethod.Post, verificationResponse, headers, cancellationToken)
                    .ConfigureAwait(false);
            }
            catch (Exception ex)
            {
                throw new PublishMessageException("Error when publishing messages", new Details { Code = "Verification.Job.Error.PublishMessage", Name = "Verification Job", Value = ex.Message });
            }
        }
    }
}
