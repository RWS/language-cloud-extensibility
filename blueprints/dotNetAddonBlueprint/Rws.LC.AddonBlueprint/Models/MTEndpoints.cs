using System.Text.Json.Serialization;

namespace Rws.LC.AddonBlueprint.Models
{
    public class MTEndpoints
    {
        /// <summary>
        /// The Machine Translation endpoint for translating content.
        /// </summary>
        [JsonPropertyName("lc.mtprovider.translate")]
        public string LCMTProviderTranslate { get; set; }

        /// <summary>
        /// The Machine Translation endpoint for retreiving the available engines.
        /// </summary>
        [JsonPropertyName("lc.mtprovider.engines")]
        public string LCMTProviderEngines { get; set; }
    }
}