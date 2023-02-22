using Rws.LC.AddonBlueprint.Enums;
using Rws.LC.AddonBlueprint.Interfaces;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text.Json.Nodes;

namespace Rws.LC.AddonBlueprint.Services
{
    /// <summary>
    /// Used to return the addon descriptor
    /// </summary>
    public class DescriptorService : IDescriptorService
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="DescriptorService"/> class.
        /// </summary>
        public DescriptorService()
        {
            // Reading from the descriptor.json file, the descriptor for this Add-On. 
            // Customize it to represent your Add-On behavior.
            string descriptorText = File.ReadAllText("descriptor.json");
            _addonDescriptor = JsonNode.Parse(descriptorText);
        }

        /// <summary>
        /// The addon descriptor.
        /// </summary>
        private readonly JsonNode _addonDescriptor;

        /// <summary>
        /// The secret data type.
        /// </summary>
        private const DataTypeEnum SecretDataType = DataTypeEnum.secret;

        /// <summary>
        /// The secret mask const string.
        /// </summary>
        private const string SecretMask = "*****";

        /// <summary>
        /// Gets the descriptor.
        /// </summary>
        /// <returns></returns>
        public JsonNode GetDescriptor()
        {
            foreach (var configuration in _addonDescriptor["configurations"].AsArray().Where(c => c["dataType"].ToString() == SecretDataType.ToString()))
            {
                configuration["defaultValue"] = SecretMask;
            }

            return _addonDescriptor;
        }

        /// <summary>
        /// Gets the secret configurations ids.
        /// </summary>
        /// <returns></returns>
        public List<string> GetSecretConfigurations()
        {
            return _addonDescriptor["configurations"].AsArray().Where(c => c["dataType"].ToString() == SecretDataType.ToString()).Select(s => s["id"].ToString()).ToList();
        }
    }
}
