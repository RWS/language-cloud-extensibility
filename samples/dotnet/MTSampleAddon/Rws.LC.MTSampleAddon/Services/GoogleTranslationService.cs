using Google.Api.Gax.ResourceNames;
using Google.Cloud.Translate.V3;
using Microsoft.CodeAnalysis;
using Rws.LC.MTSampleAddon.Interfaces;
using Rws.LC.MTSampleAddon.Models;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Rws.LC.MTSampleAddon.Services
{
    /// <summary>
    /// A real implementation for <see cref="ITranslationService"></see> that uses the Google MT provider to translate the content from Language Cloud
    /// </summary>
    public class GoogleTranslationService : ITranslationService
    {
        /// <summary>
        /// The account settings repository.
        /// </summary>
        private readonly IRepository _repository;

        public GoogleTranslationService(IRepository respository)
        {
            _repository = respository;
        }

        /// <inheritdoc />
        public async Task<TranslationEnginesResult> GetTranslationEngines(string tenantId, TranslationEnginesRequestModel translationEnginesRequest)
        {
            string sourceLanguage = translationEnginesRequest.SourceLanguage;
            List<string> targetLanguages = translationEnginesRequest.TargetLanguage;
            bool exactMatch = translationEnginesRequest.ExactMatch;

            // get stored Google settings by tenantId
            var accountSettings = await _repository.GetAccountInfoByTenantId(tenantId);
            string credentials = accountSettings.GetServiceKey();
            string projectId = accountSettings.GetProjectId();

            // prepare the translation service client
            TranslationServiceClientBuilder b = new TranslationServiceClientBuilder();
            b.JsonCredentials = credentials;
            var client = await b.BuildAsync();

            // retrieve the supported languages from Google MT
            var supportedLanguages = await client.GetSupportedLanguagesAsync($"projects/{projectId}", "", "");
            // split the source and target languages
            var supportedSourceLanguages = supportedLanguages.Languages.Where(l => l.SupportSource).ToList();
            var supportedTargetLanguages = supportedLanguages.Languages.Where(l => l.SupportTarget).ToList();

            var result = new TranslationEnginesResult(new List<TranslationEngineModel>());
            // extract the source language from the available source languages taking into consideration the exactMatch boolean
            var source = MatchLanguage(supportedSourceLanguages, sourceLanguage, exactMatch);
            // if no source language was found among the supported languages return an empty result object
            if (source == null)
            {
                return result;
            }

            Dictionary<string, List<string>> matchingTargetLanguages = ExtractMatchingTargetLanguages(targetLanguages, exactMatch, supportedTargetLanguages);

            // build the transfer object result
            foreach (var pair in matchingTargetLanguages)
            {
                // add a new TranslationEngineModel
                // where the pair.Key(e.g. "en") is the matching target language while the pair.Value is the set of target languages variants(e.g. ["en-US", "en-UK"])
                result.Items.Add(new TranslationEngineModel
                {
                    EngineSourceLanguage = source,
                    EngineTargetLanguage = pair.Key,
                    MatchingSourceLanguage = sourceLanguage,
                    MatchingTargetLanguages = pair.Value,
                    Id = $"{source}_{pair.Key}",
                    Model = "nmt"
                });
            }

            result.ItemCount = result.Items.Count;
            return result;
        }

        /// <inheritdoc />
        public async Task<TranslationsModel> Translate(string tenantId, TranslationRequestModel translationRequest)
        {
            // get stored Google settings by tenantId
            var accountSettings = await _repository.GetAccountInfoByTenantId(tenantId);
            string credentials = accountSettings.GetServiceKey();
            string projectId = accountSettings.GetProjectId();

            // prepare the translation service client
            TranslationServiceClientBuilder b = new TranslationServiceClientBuilder();
            b.JsonCredentials = credentials;
            var client = await b.BuildAsync();

            // parse the engineId
            string[] split = translationRequest.EngineId.Split('_');
            string sourceLanguage = split[0];
            string targetLanguage = split[1];

            // prepare the translation request
            TranslateTextRequest request = new TranslateTextRequest
            {
                SourceLanguageCode = sourceLanguage,
                TargetLanguageCode = targetLanguage,
                Parent = new ProjectName(projectId).ToString()
            };
            request.Contents.AddRange(translationRequest.Contents);

            // perform the translation request
            var googleTranslation = client.TranslateText(request);

            // parse the translation result from Google
            var result = new TranslationsModel
            {
                Translations = googleTranslation.Translations.Select(x => x.TranslatedText).ToList()
            };

            return result;
        }

        /// <summary>
        /// Creates a dictionary with matching requested target languages grouped by the the matching target language
        /// E.g. of key value pair: "en" -> [ "en-US", "en-UK" ]
        /// </summary>
        /// <param name="targetLanguages">The requested target languages</param>
        /// <param name="exactMatch">Indicates whether the target language should match exactly the requested language</param>
        /// <param name="supportedTargetLanguages">The languages retrieved from Google</param>
        /// <returns></returns>
        private Dictionary<string, List<string>> ExtractMatchingTargetLanguages(List<string> targetLanguages, bool exactMatch, List<SupportedLanguage> supportedTargetLanguages)
        {
            Dictionary<string, List<string>> matchingTargetLanguages = new Dictionary<string, List<string>>();
            // iterate through the requested target languages
            foreach (string targetLanguage in targetLanguages)
            {
                // extract the targetLanguage from the available target languages taking into consideration the exactMatch boolean
                var target = MatchLanguage(supportedTargetLanguages, targetLanguage, exactMatch);

                if (target != null)
                {
                    List<string> existingTargets;
                    // check if there are already existing targets for the found target key
                    if (matchingTargetLanguages.TryGetValue(target, out existingTargets))
                    {
                        // if there are existing targets append them with the new matching target language
                        existingTargets.Add(targetLanguage);
                    }
                    else
                    {
                        // if no other key was found initialize a new list
                        existingTargets = new List<string> { targetLanguage };
                    }
                    // finally, add or replace with the new found target list
                    matchingTargetLanguages[target] = existingTargets;
                }
            }

            return matchingTargetLanguages;
        }

        /// <summary>
        /// Searches the requested language in the available languages.
        /// </summary>
        /// <param name="languages">The languages retrieved from Google</param>
        /// <param name="requestedLanguage">The requested language code</param>
        /// <param name="exactMatch">Indicates whether the language should match exactly the requested language</param>
        /// <returns></returns>
        private string MatchLanguage(List<SupportedLanguage> languages, string requestedLanguage, bool exactMatch)
        {
            // check if the available languages include the exact value of requestedLanguage
            var match = languages.SingleOrDefault(x => x.LanguageCode == requestedLanguage);

            // if not found and exactMatch was not requested we search by the language's short form
            if (match == null && !exactMatch)
            {
                // get partial matches
                string lang = requestedLanguage.Split('-').First(); // en-US -> en
                match = languages.SingleOrDefault(x => x.LanguageCode == lang);
            }

            return match.LanguageCode;
        }
    }
}
