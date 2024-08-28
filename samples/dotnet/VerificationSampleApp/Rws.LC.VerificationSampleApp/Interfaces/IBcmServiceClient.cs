using Sdl.Core.Bcm.BcmModel.PartialSerialization;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Interfaces
{
    public interface IBcmServiceClient
    {
        public Task<IPartialBcmSerializer> DownloadBcmDocument(string bcmDownloadUrl, CancellationToken token);
    }
}
