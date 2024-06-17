using System.Collections.Generic;

namespace Rws.LC.AppBlueprint.Models
{
    public class TranslationEnginesResult
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="TranslationEngineModel"/> class.
        /// </summary>
        /// <param name="translationEngines">The list of translation engines.</param>
        public TranslationEnginesResult(List<TranslationEngineModel> translationEngines)
        {
            this.Items = translationEngines;
            this.ItemCount = translationEngines.Count;
        }

        /// <summary>
        /// The list of translation engines.
        /// </summary>
        public List<TranslationEngineModel> Items { get; set; }

        /// <summary>
        /// The item count.
        /// </summary>
        public int ItemCount { get; set; }
    }
}
