using Rws.LC.VerificationSampleApp.Verifiers.Events;
using Rws.LC.VerificationSampleApp.Verifiers.Interfaces;
using Rws.LC.VerificationSampleApp.Verifiers.Models;
using Rws.LC.VerificationSampleApp.Verifiers.Other;
using Sdl.Core.Bcm.BcmModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Verifiers.Verifiers
{
    public class SegmentLengthVerifier : ISegmentLengthVerifier
    {
        public string PublishMessageEndpoint { get; set; }

        public event EventHandler PublishMessage;
        public string SessionId { get; set; }
        public string RequestId { get; set; }   
        public string TraceId { get; set; }

        public SegmentLengthVerifier() {}    
        

        public SegmentLengthVerifier(string publishMessageEndpoint, string sessionId, string requestId, string traceid) 
        {
            PublishMessageEndpoint = publishMessageEndpoint;
            SessionId = sessionId;
            RequestId = requestId;
            TraceId = traceid;
        }

        public async Task<bool> Verify(SegmentPair segmentPair, SegmentIdentifierData segmentLocationData, int maxLengthToCheck, int messageId)
        {
            var segmentVisitorSource = new SegmentVisitor();
            var segmentVisitorTarget = new SegmentVisitor();
            segmentVisitorSource.VisitSegment(segmentPair.Source);
            segmentVisitorTarget.VisitSegment(segmentPair.Target);

            return await Task<bool>.Run(() =>
            {
                var sourceLength = segmentVisitorSource.SegmentTextLength;
                var targetLength = segmentVisitorTarget.SegmentTextLength;

                if (targetLength - sourceLength >= maxLengthToCheck)
                {
                    var segmentLocation = new SegmentIdentifierData
                    {
                        FileId = segmentLocationData.FileId,
                        ParagraphUnitId = segmentLocationData.ParagraphUnitId,
                        SegmentNumber = segmentLocationData.SegmentNumber
                    };

                    var location = new MessageLocation
                    {
                        FromLocation = sourceLength,
                        ToLocation = targetLength
                    };

                    var verificationMessage = new VerificationMessage
                    {
                        Id = messageId,
                        IsSource = false,
                        SegmentId = segmentPair.Source.Id,
                        SegmentLocation = segmentLocation,
                        MessageLocation = location,
                        MessageType = "lc-verification-sample.LengthCheck",
                        Verifier = "lc-verification-sample",
                        Level = "Error",
                        MessageArguments = new List<string> { segmentLocation.SegmentNumber, (location.ToLocation - location.FromLocation).ToString()}

                    };

                    var messageEventArgs = new PublishMessageEventArgs(PublishMessageEndpoint, verificationMessage, SessionId, RequestId, TraceId);
                    PublishMessage?.Invoke(this, messageEventArgs);
                    return true;
                }
                return false;
            });
        }
    }
}
