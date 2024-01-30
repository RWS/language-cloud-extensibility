using System.Collections.Generic;

namespace Rws.LC.SampleVerificationAddon.RestService.Models
{
    public class VerificationRequest
    {
        /// <summary>
        /// Input resource details
        /// </summary>
        public InputResourceDetails InputResourceDetails { get; set; }
        /// <summary>
        /// Url to make final callback on
        /// </summary>
        public string CallbackUrl { get; set; }
        /// <summary>
        /// Source language
        /// </summary>
        public string SourceLanguage { get; set; }
        /// <summary>
        /// Target language
        /// </summary>
        public string TargetLanguage { get; set; }
        /// <summary>
        /// Url used to publish batches of messages to
        /// </summary>
        public string PublishMessageUrl { get; set; }
        /// <summary>
        /// Settings to apply to this verification request
        /// </summary>
        public object VerifierSettings { get; set; }
        /// <summary>
        /// Session ID for this verification request
        /// </summary>
        public string SessionId { get; set; }
    }
}