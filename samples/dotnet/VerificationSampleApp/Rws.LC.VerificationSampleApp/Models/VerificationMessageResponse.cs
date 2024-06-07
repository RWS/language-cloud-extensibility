using Rws.LC.VerificationSampleApp.Verifiers.Models;
using System.Collections.Generic;

namespace Rws.LC.VerificationSampleApp.Models
{
    public class VerificationMessageResponse
    {
        public string SessionId { get; set; }
        public List<VerificationMessage> Messages { get; set; } 
    }
}
