using Rws.VerificationSampleAddon.RestService.Models;
using System.Collections.Generic;

namespace Rws.LC.SampleVerificationAddon.RestService.Models
{
    public class VerificationResponse
    {
        public string VerificationSessionId { get; set; }
        public string VerificationResult { get; set; }
        public List<ErrorModel> Errors { get; set; }
        public int MessageCount { get; set; }
    }
}
