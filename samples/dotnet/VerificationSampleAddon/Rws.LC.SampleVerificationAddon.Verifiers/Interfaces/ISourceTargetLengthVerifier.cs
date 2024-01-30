using Rws.LC.SampleVerificationAddon.Verifiers.Events;
using Rws.LC.SampleVerificationAddon.Verifiers.Models;
using Rws.LC.SampleVerificationAddon.Verifiers.Settings;
using Rws.LC.SampleVerificationAddon.Verifiers.Verifiers;
using Sdl.Core.Bcm.BcmModel;
using Sdl.Core.Bcm.BcmModel.PartialSerialization;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.Verifiers.Interfaces
{
    public interface ISourceTargetLengthVerifier  : IVerifier
    {
        public Task<bool> Verify(SegmentPair segmentPair, SegmentIdentifierData segmentLocationData, SourceTargetLengthVerifierSettings settings, int messageId);
    }
}
