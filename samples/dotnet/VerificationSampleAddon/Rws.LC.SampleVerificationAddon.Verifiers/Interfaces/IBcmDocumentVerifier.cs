using Rws.LC.SampleVerificationAddon.Verifiers.Settings;
using Sdl.Core.Bcm.BcmModel.PartialSerialization;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.Verifiers.Interfaces
{
    public interface IBcmDocumentVerifier : IVerifier
    {
        public Task<int> Verify(IPartialBcmSerializer bcmSerializer, SourceTargetLengthVerifierSettings settings);
    }
}
