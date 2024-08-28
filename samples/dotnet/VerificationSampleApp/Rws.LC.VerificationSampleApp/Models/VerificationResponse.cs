using Rws.LC.VerificationSampleApp.Models;
using System.Collections.Generic;

namespace Rws.LC.VerificationSampleApp.Models
{
    public class VerificationResponse
    {
        public string VerificationSessionId { get; set; }
        public string VerificationResult { get; set; }
        public List<ErrorModel> Errors { get; set; }
        public int MessageCount { get; set; }
    }
}
