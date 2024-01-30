namespace Rws.LC.SampleVerificationAddon.RestService.Models
{
    public class InputResourceDetails
    {
        /// <summary>
        /// URL for downloading the Native file
        /// </summary>
        public string NativeFileUrl { get; set; }
        /// <summary>
        /// Url for downloading the Native file with annotations
        /// </summary>
        public string NativeAnnotatedFileUrl { get; set; }
        /// <summary>
        /// Url for downloading the Bilingual document
        /// </summary>
        public string BilingualDocumentUrl { get; set; }
        /// <summary>
        /// Bilingual document version
        /// </summary>
        public int BilingualDocumentVersion { get; set; }
        /// <summary>
        /// ID of the Language Resource Template
        /// </summary>
        public string LanguageResourceTemplateId { get; set; }
        /// <summary>
        /// Url for downloading the Verification Resource Package
        /// </summary>
        public string VerificationResourcePackageUrl { get; set; }
        /// <summary>
        /// Translation Profile ID
        /// </summary>
        public string TranslationProfileId { get; set; }
        
    }
}
