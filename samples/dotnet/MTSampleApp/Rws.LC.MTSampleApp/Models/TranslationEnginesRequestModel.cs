using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace Rws.LC.MTSampleApp.Models
{
    public class TranslationEnginesRequestModel
    {
        /// <summary>
        /// The engine model
        /// Ex: NMT
        /// </summary>
        public string Model { get; set; }

        /// <summary>
        /// The source language
        /// </summary>
        [Required]
        public string SourceLanguage { get; set; }

        /// <summary>
        /// The target languages
        /// </summary>
        [Required]
        public List<string> TargetLanguage { get; set; }

        /// <summary>
        /// Flag to whether include glossaries or not
        /// </summary>
        public bool IncludeGlossaries { get; set; }

        /// <summary>
        /// The exact match flag
        /// </summary>
        public bool ExactMatch { get; set; }
    }
}
