using Rws.LC.SampleVerificationAddon.RestService.Models;
using Rws.LC.SampleVerificationAddon.Verifiers.Models;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.RestService.Interfaces
{
    public interface IVerificationServiceClient
    {
        public Task PublishVerificationMessages(string publishMessageEndpoint, VerificationMessageResponse verificationResponse, string requestId, string traceId, CancellationToken cancellationToken);
    }
}
