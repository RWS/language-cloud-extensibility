using Newtonsoft.Json;
using Rws.LC.SampleVerificationAddon.Verifiers.Events;
using Rws.LC.SampleVerificationAddon.Verifiers.Interfaces;
using Rws.LC.SampleVerificationAddon.Verifiers.Models;
using Rws.LC.SampleVerificationAddon.Verifiers.Settings;
using Sdl.Core.Bcm.BcmModel;
using Sdl.Core.Bcm.BcmModel.PartialSerialization;

namespace Rws.LC.SampleVerificationAddon.Verifiers.Verifiers
{
    public class BcmDocumentVerifier : IBcmDocumentVerifier
    {
        public event EventHandler PublishMessage;
        public string PublishMessageEndpoint { get; set; }
        public string SessionId { get; set; }
        public string RequestId { get; set; }
        public string TraceId { get; set; }

        private SourceTargetLengthVerifierSettings Settings { get; set; }

        private int _messageId;

        public BcmDocumentVerifier(string publishMessageEndpoint, string sessionId, string requestId, string traceId)
        {
            PublishMessageEndpoint = publishMessageEndpoint;
            SessionId = sessionId;
            RequestId = requestId;
            TraceId = traceId;
            _messageId = 1;
        }

        public async Task<int> Verify(IPartialBcmSerializer bcmSerializer, SourceTargetLengthVerifierSettings settings)
        {
            Settings = settings;
            var fileIds = bcmSerializer.GetFileIds();
            foreach(var fileId in fileIds)
            {
                using(var fileSerializer = bcmSerializer.GetFileSerializer(fileId))
                {
                    await ProcessParagraphUnits(fileSerializer);
                }
            }
            var endRequest = new PublishMessageEventArgs(PublishMessageEndpoint, new VerificationMessage(), SessionId, RequestId, TraceId, true);
            PublishMessage?.Invoke(this, endRequest);
            return _messageId;
        }
      

        private async Task ProcessParagraphUnits(IPartialFileSerializer fileSerializer)
        {
            foreach(var paragraphUnit in fileSerializer.GetAllParagraphUnits()) 
            {
                await ProcessSegmentPairs(paragraphUnit.SegmentPairs);
            }
        }

        private async Task ProcessSegmentPairs(IEnumerable<SegmentPair> segmentPairs)
        {
            foreach(var segmentPair in segmentPairs)
            {
                var segmentLocationData = new SegmentIdentifierData
                {
                    FileId = segmentPair.Source.ParentParagraphUnit.ParentFileId,
                    ParagraphUnitId = segmentPair.Source.ParentParagraphUnit.Id,
                    SegmentNumber = segmentPair.Source.SegmentNumber
                };
                var sourceTargetLengthVerifier = new SourceTargetLengthVerifier(PublishMessageEndpoint, SessionId, RequestId, TraceId);
                sourceTargetLengthVerifier.PublishMessage += PublishMessage;
                var result = await sourceTargetLengthVerifier.Verify(segmentPair, segmentLocationData, Settings, _messageId);
                if(result)
                {
                    _messageId++;
                }
                sourceTargetLengthVerifier.PublishMessage -= PublishMessage;
            }
        }
    }
}
