using Rws.LC.SampleVerificationAddon.Verifiers.Models;
using Sdl.Core.Bcm.BcmModel;
using System.Collections.Generic;

namespace Rws.LC.SampleVerificationAddon.RestService.Models
{
    public class VerifySegmentRequest
    {
        public object Fragment { get; set; }
        public string LanguageResourceTemplateId { get; set; }
        public string TranslationProfileId { get; set; }
        public object VerifierSettings { get; set; }
        public string SourceLanguage { get; set; }
        public string TargetLanguage { get; set; }

        public SegmentIdentifierData SegmentLocation { get; set; }  

    }
}
