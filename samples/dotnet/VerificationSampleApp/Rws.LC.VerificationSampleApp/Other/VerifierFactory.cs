using Rws.LC.VerificationSampleApp.Verifiers.Interfaces;
using Rws.LC.VerificationSampleApp.Verifiers.Verifiers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Verifiers.Other
{
    public class VerifierFactory : IVerifierFactory
    {
        public IBcmDocumentVerifier GetBilingualDocumentVerifier(string publishMessageEndpoint, string sessionId, string requestId, string traceId)
        {
            return new BcmDocumentVerifier(publishMessageEndpoint, sessionId, requestId, traceId);
        }
        public ISourceTargetLengthVerifier GetSegmentVerifier()
        {
            return new SourceTargetLengthVerifier(null, null, null, null);
        }
        public ISourceTargetLengthVerifier GetSegmentVerifier(string publishMessageEndpoint, string sessionId, string requestId, string traceId)
        {
            return new SourceTargetLengthVerifier(publishMessageEndpoint, sessionId, requestId, traceId);
        }
    }
}
