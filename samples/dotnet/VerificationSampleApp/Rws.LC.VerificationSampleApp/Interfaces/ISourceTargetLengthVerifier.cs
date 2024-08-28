using Rws.LC.VerificationSampleApp.Verifiers.Events;
using Rws.LC.VerificationSampleApp.Verifiers.Models;
using Rws.LC.VerificationSampleApp.Verifiers.Settings;
using Rws.LC.VerificationSampleApp.Verifiers.Verifiers;
using Sdl.Core.Bcm.BcmModel;
using Sdl.Core.Bcm.BcmModel.PartialSerialization;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Verifiers.Interfaces
{
    public interface ISourceTargetLengthVerifier  : IVerifier
    {
        public Task<bool> Verify(SegmentPair segmentPair, SegmentIdentifierData segmentLocationData, SourceTargetLengthVerifierSettings settings, int messageId);
    }
}
