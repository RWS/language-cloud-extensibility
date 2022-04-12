using System.Text.Json.Serialization;

namespace Rws.LC.AddonBlueprint.Models
{
    public class AutomaticTaskEndpoints
    {
        /// <summary>
        /// The LanguageCloud Automatic Task Submit endpoint.
        /// </summary>
        [JsonPropertyName("lc.automatictask.submit")]
        public string LCAutomaticTaskSubmit { get; set; }
    }
}
