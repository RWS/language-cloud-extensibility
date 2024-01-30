using Rws.LC.SampleVerificationAddon.Verifiers.Events;
using Rws.LC.SampleVerificationAddon.Verifiers.Interfaces;
using Rws.LC.SampleVerificationAddon.Verifiers.Models;
using Rws.LC.SampleVerificationAddon.Verifiers.Settings;
using Sdl.Core.Bcm.BcmModel;
using Sdl.Core.Bcm.BcmModel.PartialSerialization;

namespace Rws.LC.SampleVerificationAddon.Verifiers.Verifiers
{
    public class SourceTargetLengthVerifier : ISourceTargetLengthVerifier
    {
        public string PublishMessageEndpoint { get; set; }

        public event EventHandler PublishMessage;
        public string SessionId { get; set; } 
        public string RequestId { get; set; }
        public string TraceId { get; set; }


        public SourceTargetLengthVerifier(string publishMessageEndpoint, string sessionId, string requestId, string traceId)
        {
            PublishMessageEndpoint = publishMessageEndpoint;
            SessionId = sessionId;
            RequestId = requestId;
            TraceId = traceId;
        }
        public async Task<bool> Verify(SegmentPair segmentPair, SegmentIdentifierData segmentLocationData, SourceTargetLengthVerifierSettings settings, int messageId)
        {

            var lengthCheckCharacterLimit = settings == null ? 1 : settings.LengthCheckCharacterLimit;

            var segmentVerifier = new SegmentLengthVerifier(PublishMessageEndpoint, SessionId, RequestId, TraceId);

            segmentVerifier.PublishMessage += PublishMessage;

            var result = await segmentVerifier.Verify(segmentPair, segmentLocationData, lengthCheckCharacterLimit, messageId);

            segmentVerifier.PublishMessage -= PublishMessage;

            return result;
            
        }

    }
}