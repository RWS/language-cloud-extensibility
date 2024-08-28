using System.Threading;
using Rws.LC.VerificationSampleApp.Models;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Interfaces
{
    public interface IExternalJobServiceClient
    {
        public Task SendCallback(string callbackUrl, VerificationResponse response, string tenantId, CancellationToken cancellationToken);
    }
}
