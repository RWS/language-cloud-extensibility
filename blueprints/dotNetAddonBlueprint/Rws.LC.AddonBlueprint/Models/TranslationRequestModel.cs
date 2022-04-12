using System.Collections.Generic;

namespace Rws.LC.AddonBlueprint.Models
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
    }
}
