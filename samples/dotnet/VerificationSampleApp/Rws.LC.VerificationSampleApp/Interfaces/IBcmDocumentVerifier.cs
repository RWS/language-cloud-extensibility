using Rws.LC.VerificationSampleApp.Verifiers.Settings;
using Sdl.Core.Bcm.BcmModel.PartialSerialization;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Verifiers.Interfaces
{
    public interface IBcmDocumentVerifier : IVerifier
    {
        public Task<int> Verify(IPartialBcmSerializer bcmSerializer, SourceTargetLengthVerifierSettings settings);
    }
}
