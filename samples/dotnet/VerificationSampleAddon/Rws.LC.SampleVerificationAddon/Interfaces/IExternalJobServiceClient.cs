using System.Threading;
using Rws.LC.SampleVerificationAddon.RestService.Models;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.RestService.Interfaces
{
    public interface IExternalJobServiceClient
    {
        public Task SendCallback(string callbackUrl, VerificationResponse response, string tenantId, CancellationToken cancellationToken);
    }
}
