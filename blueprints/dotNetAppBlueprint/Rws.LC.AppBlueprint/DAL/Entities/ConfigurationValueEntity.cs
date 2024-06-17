using System.Text.Json.Nodes;

namespace Rws.LC.AppBlueprint.DAL.Entities
{
    public class ConfigurationValueEntity
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
