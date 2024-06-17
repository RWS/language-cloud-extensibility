using System.Collections.Generic;

namespace Rws.LC.AppBlueprint.Models
{
    public class TranslationRequestModel
    {
        /// <summary>
        /// The MT engine id.
        /// </summary>
        public string EngineId { get; set; }

        /// <summary>
        /// The input text to translate as html.
        /// </summary>
        public List<string> Contents { get; set; }

        /// <summary>
        /// The project identifier.
        /// </summary>
        public string ProjectId { get; set; }

        /// <summary>
        /// The source file identifier.
        /// </summary>
        public string SourceFileId { get; set; }

        /// <summary>
        /// The target file identifier.
        /// </summary>
        public string TargetFileId { get; set; }
    }
}
