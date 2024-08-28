using System.Text.Json.Nodes;

namespace Rws.LC.VerificationSampleApp.Models
{
    public class ConfigurationValueModel
    {
        /// <summary>
        /// The identifier.
        /// </summary>
        public string Id { get; set; }

        /// <summary>
        /// The value object.
        /// </summary>
        public JsonNode Value { get; set; }
    }
}
