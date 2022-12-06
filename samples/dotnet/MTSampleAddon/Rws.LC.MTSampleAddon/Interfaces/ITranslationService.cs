using Rws.LC.MTSampleAddon.Models;
using System.Threading.Tasks;

namespace Rws.LC.MTSampleAddon.Interfaces
{
    public interface ITranslationService
    {
        /// <summary>
        /// Retrieves the available translation engines based on the requested languages.
        /// </summary>
        /// <param name="tenantId">The tenant id from the context</param>
        /// <param name="translationEnginesRequest">The translation engines request</param>
        /// <returns>The translation engines</returns>
        Task<TranslationEnginesResult> GetTranslationEngines(string tenantId, TranslationEnginesRequestModel translationEnginesRequest);

        /// <summary>
        /// Translates the received content.
        /// </summary>
        /// <param name="tenantId">The tenant id from the context</param>
        /// <param name="translationRequest">The translation request</param>
        /// <returns>A list of translations</returns>
        Task<TranslationsModel> Translate(string tenantId, TranslationRequestModel translationRequest);
    }
}
