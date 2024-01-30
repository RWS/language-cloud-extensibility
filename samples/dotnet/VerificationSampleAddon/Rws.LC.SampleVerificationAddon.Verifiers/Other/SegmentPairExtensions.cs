using Sdl.Core.Bcm.BcmModel;

namespace Rws.LC.SampleVerificationAddon.Verifiers.Other
{
    public static class SegmentPairExtensions
    {
        public static IEnumerable<SegmentPair> GetSegmentPairs(this Fragment fragment)
        {
            Paragraph paragraph = fragment.SourceContent as Paragraph;
            Paragraph paragraph2 = fragment.TargetContent as Paragraph;
            if (paragraph == null && paragraph2 == null)
            {
                Segment segment = fragment.SourceContent as Segment;
                Segment segment2 = fragment.TargetContent as Segment;
                if (segment == null && segment2 == null)
                {
                    throw new InvalidOperationException("Source or target content has to be segment or paragraph!");
                }

                return new List<SegmentPair>
                {
                    new SegmentPair(segment, segment2)
                };
            }

            ParagraphUnit paragraphUnit = new ParagraphUnit
            {
                Source = paragraph,
                Target = paragraph2
            };
            return paragraphUnit.SegmentPairs;
        }
    }
}
