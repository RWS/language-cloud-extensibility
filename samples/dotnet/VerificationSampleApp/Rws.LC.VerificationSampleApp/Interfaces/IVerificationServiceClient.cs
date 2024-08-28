using Rws.LC.VerificationSampleApp.Models;
using Rws.LC.VerificationSampleApp.Verifiers.Models;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Interfaces
{
    public interface IVerificationServiceClient
    {
        public Task PublishVerificationMessages(string publishMessageEndpoint, VerificationMessageResponse verificationResponse, string requestId, string traceId, CancellationToken cancellationToken);
    }
}
