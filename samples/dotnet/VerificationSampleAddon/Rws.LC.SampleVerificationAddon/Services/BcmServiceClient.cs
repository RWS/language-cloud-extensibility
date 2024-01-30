using Rws.LC.AddonSample.Preview.Helpers;
using Rws.LC.SampleVerificationAddon.RestService.Helpers;
using Rws.LC.SampleVerificationAddon.RestService.Interfaces;
using Sdl.Core.Bcm.BcmModel.PartialSerialization;
using System.IO;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.RestService.Services
{
    public class BcmServiceClient : RestClientBase, IBcmServiceClient
    {
        public async Task<IPartialBcmSerializer> DownloadBcmDocument(string bcmDownloadUrl, CancellationToken token)
        {
            var response = await SendAsync(bcmDownloadUrl, HttpMethod.Get, null, null, token);
            var partialBcmSerializer = new PartialBcmSerializerFactory().CreateSerializer(await response.Content.ReadAsStreamAsync());
            return partialBcmSerializer;
        }
    }
}
