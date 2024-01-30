using Rws.LC.SampleVerificationAddon.Verifiers.Models;
using System.Collections.Generic;

namespace Rws.LC.SampleVerificationAddon.RestService.Models
{
    public class VerificationMessageResponse
    {
        public string SessionId { get; set; }
        public List<VerificationMessage> Messages { get; set; } 
    }
}
