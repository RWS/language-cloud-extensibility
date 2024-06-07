using Sdl.Core.Bcm.BcmModel.Annotations;
using Sdl.Core.Bcm.BcmModel.Common;
using Sdl.Core.Bcm.BcmModel;

namespace Rws.LC.VerificationSampleApp.Verifiers.Other
{
    public class SegmentVisitor : BcmVisitor
    {
        public int SegmentTextLength { get; set; }
        public string CurrentSegmentNumber { get; private set; }

        public void VisitChildren(MarkupDataContainer container)
        {
            container.ForEach(x => x.AcceptVisitor(this));
        }

        public override void VisitStructure(StructureTag structureTag)
        {
        }

        public override void VisitTagPair(TagPair tagPair)
        {
            VisitChildren(tagPair);
        }

        public override void VisitPlaceholderTag(PlaceholderTag tag)
        {
        }

        public override void VisitText(TextMarkup text)
        {
            SegmentTextLength += text.Text.Length;
        }

        public override void VisitSegment(Segment segment)
        {
            SegmentTextLength = 0;
            CurrentSegmentNumber = segment.SegmentNumber;

            VisitChildren(segment);

            CurrentSegmentNumber = string.Empty;
        }

        public override void VisitCommentContainer(CommentContainer commentContainer)
        {
            VisitChildren(commentContainer);
        }

        public override void VisitLockedContentContainer(LockedContentContainer lockedContentContainer)
        {
            VisitChildren(lockedContentContainer);
        }

        public override void VisitRevisionContainer(RevisionContainer revisionContainer)
        {
        }

        public override void VisitFeedbackContainer(FeedbackContainer feedbackContainer)
        {
        }

        public override void VisitParagraph(Paragraph paragraph)
        {
            VisitChildren(paragraph);
        }

        public override void VisitTerminologyContainer(TerminologyAnnotationContainer terminologyAnnotation)
        {
            VisitChildren(terminologyAnnotation);
        }
    }
}
