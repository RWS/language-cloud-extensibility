using Rws.LC.SampleVerificationAddon.Verifiers.Models;
using Sdl.Core.Bcm.BcmModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.Verifiers.Interfaces
{
    public interface ISegmentLengthVerifier : IVerifier
    {
        public Task<bool> Verify(SegmentPair segmentPair, SegmentIdentifierData segmentLocationData, int maxLengthToCheck, int messageId);
    }
}
