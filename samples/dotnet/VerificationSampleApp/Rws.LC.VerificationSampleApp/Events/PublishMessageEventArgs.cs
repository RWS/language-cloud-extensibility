using Rws.LC.VerificationSampleApp.Verifiers.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Verifiers.Events
{
    public class PublishMessageEventArgs : EventArgs
    {
        public VerificationMessage Message { get; set; }
        public string PublishMessageEndpoint { get; set; } 
        public string SessionId { get; set; }
        public string RequestId { get; set; }
        public string TraceId { get; set; }
        public bool IsEndRequest { get; set; }

        public PublishMessageEventArgs(string publishMessageEndpoint, VerificationMessage message, string sessionId, string requestId, string traceId, bool isEndRequest = false) 
        { 
            Message = message;
            PublishMessageEndpoint = publishMessageEndpoint;
            SessionId = sessionId;
            RequestId = requestId;
            TraceId = traceId;
            IsEndRequest = isEndRequest;
        }
    }
}
