using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Rws.LC.MTSampleAddon.Enums;

namespace Rws.LC.MTSampleAddon.Models
{
    public class AddonConfigurationModel
    {
        /// <summary>
        /// The name.
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// The identifier.
        /// </summary>
        public string Id { get; set; }

        /// <summary>
        /// The description.
        /// </summary>
        public string Description { get; set; }

        /// <summary>
        /// The flag indicating whether this setting is optional or not.
        /// </summary>
        public bool Optional { get; set; }

        /// <summary>
        /// The default value.
        /// </summary>
        public object DefaultValue { get; set; }

        /// <summary>
        /// The data type.
        /// </summary>
        [JsonConverter(typeof(StringEnumConverter))]
        public DataTypeEnum DataType { get; set; }
    }
}
