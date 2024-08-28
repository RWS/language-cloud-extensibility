using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Verifiers.Interfaces
{
    public interface IVerifierFactory
    {
        public IBcmDocumentVerifier GetBilingualDocumentVerifier(string publishMessageEndpoint, string sessionId, string requestId, string traceId);
        public ISourceTargetLengthVerifier GetSegmentVerifier();
        public ISourceTargetLengthVerifier GetSegmentVerifier(string publishMessageEndpoint, string sessionId, string requestId, string traceId);

        
    }
}
