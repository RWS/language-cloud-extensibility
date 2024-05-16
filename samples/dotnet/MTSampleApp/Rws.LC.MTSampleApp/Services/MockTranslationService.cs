using Rws.LC.MTSampleApp.Interfaces;
using Rws.LC.MTSampleApp.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Xml;

namespace Rws.LC.MTSampleApp.Services
{
    /// <summary>
    /// A mock implementation for <see cref="ITranslationService"></see> that can be used without the having to provide real configuration settings
    /// </summary>
    public class MockTranslationService : ITranslationService
    {
        /// <inheritdoc />
        public Task<TranslationEnginesResult> GetTranslationEngines(string tenantId, TranslationEnginesRequestModel translationEnginesRequest)
        {
            string sourceLanguage = translationEnginesRequest.SourceLanguage;
            List<string> targetLanguages = translationEnginesRequest.TargetLanguage;

            var result = new TranslationEnginesResult(new List<TranslationEngineModel>());

            // consider all the requested languages as exact matches
            foreach (string targetLanguage in targetLanguages)
            {
                var source = sourceLanguage;
                var target = targetLanguage;

                result.Items.Add(new TranslationEngineModel
                {
                    EngineSourceLanguage = source,
                    EngineTargetLanguage = target,
                    MatchingSourceLanguage = sourceLanguage,
                    MatchingTargetLanguages = new List<string> { targetLanguage },
                    Id = $"{source}_{target}",
                    Model = "nmt"
                });
            }

            result.ItemCount = result.Items.Count;
            // return as Task to comply with ITranslationService
            return Task.FromResult(result);
        }

        /// <inheritdoc />
        public Task<TranslationsModel> Translate(string tenantId, TranslationRequestModel translationRequest)
        {
            List<string> translations = new List<string>();
            foreach (var content in translationRequest.Contents)
            {
                translations.Add(TranslateHtml(content));
            }

            var result = new TranslationsModel
            {
                Translations = translations
            };
            // return as Task to comply with ITranslationService
            return Task.FromResult(result);
        }

        /// <summary>
        /// Translates the content by reversing the text.
        /// </summary>
        /// <param name="html">The html content to translate</param>
        /// <returns>The translated html</returns>
        private static string TranslateHtml(string html)
        {
            string translated = html;
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(html);

            foreach (XmlNode node in doc.DocumentElement.ChildNodes)
            {
                if (!string.IsNullOrWhiteSpace(node.InnerText))
                {
                    string translation = new(node.InnerText.Reverse().ToArray());
                    translated = translated.Replace(node.InnerText, translation);
                }
            }

            return translated;
        }
    }
}
