using System.Collections.Generic;

namespace Rws.LC.MTSampleAddon.Models
{
    public class ConfigurationSettingsResult
    {
        /// <summary>
        /// Initializes an empty instance of the <see cref="ConfigurationSettingsResult"/> class.
        /// Needed for deserialization in tests.
        /// </summary>
        public ConfigurationSettingsResult()
        { 
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="ConfigurationSettingsResult"/> class.
        /// </summary>
        /// <param name="configurationValues">The list of configuration values.</param>
        public ConfigurationSettingsResult(List<ConfigurationValueModel> configurationValues)
        {
            this.Items = configurationValues;
            this.ItemCount = configurationValues.Count;
        }

        /// <summary>
        /// The configuration values.
        /// </summary>
        public List<ConfigurationValueModel> Items { get; set; }

        /// <summary>
        /// The item count.
        /// </summary>
        public int ItemCount { get; set; }
    }
}
