using Rws.LC.SampleVerificationAddon.Verifiers.Models;
using System.Collections.Generic;

namespace Rws.LC.SampleVerificationAddon.RestService.Models
{
    public class VerifySegmentResponse
    {
        public List<VerificationMessage> VerificationMessages { get; set; }
    }
}
