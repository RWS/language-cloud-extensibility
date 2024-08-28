using System;
using System.Collections.Generic;

namespace Rws.LC.VerificationSampleApp.Verifiers.Models
{
    public class VerificationMessage
    {
        public int Id { get; set; }
        public string MessageType { get; set; } 
        public string Verifier { get; set; }
        public string Level { get; set; }
        public string SegmentId { get; set; }
        public string TagId { get; set; }
        public bool IsSource { get; set; }
        public List<string> MessageArguments { get; set; }
        public SegmentIdentifierData SegmentLocation { get; set; }
        public MessageLocation MessageLocation { get; set; }   
    }
}
