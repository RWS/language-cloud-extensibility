using System.Collections.Generic;

namespace Rws.LC.VerificationSampleApp.Models
{
    public class TranslationEngineModel
    {
        /// <summary>
        /// The Id that uniquely identifies this engine id in the app.
        /// </summary>
        public string Id { get; set; }

        /// <summary>
        /// The model.
        /// </summary>
        public string Model { get; set; }

        /// <summary>
        /// The engine source language.
        /// </summary>
        public string EngineSourceLanguage { get; set; }

        /// <summary>
        /// The engine target language.
        /// </summary>
        public string EngineTargetLanguage { get; set; }

        /// <summary>
        /// The matching source language.
        /// </summary>
        public string MatchingSourceLanguage { get; set; }

        /// <summary>
        /// The matching target languages.
        /// </summary>
        public List<string> MatchingTargetLanguages { get; set; }

        /// <summary>
        /// The glossaries.
        /// </summary>
        public List<Glossary> Glossaries { get; set; }
    }
}
