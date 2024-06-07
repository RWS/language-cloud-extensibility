using Rws.LC.VerificationSampleApp.Enums;
using Rws.LC.VerificationSampleApp.Interfaces;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text.Json.Nodes;

namespace Rws.LC.VerificationSampleApp.Services
{
    /// <summary>
    /// Used to return the app descriptor
    /// </summary>
    public class DescriptorService : IDescriptorService
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="DescriptorService"/> class.
        /// </summary>
        public DescriptorService()
        {
            // Reading from the descriptor.json file, the descriptor for this app. 
            // Customize it to represent your app behavior.
            string descriptorText = File.ReadAllText("descriptor.json");
            _appDescriptor = JsonNode.Parse(descriptorText);
        }

        /// <summary>
        /// The app descriptor.
        /// </summary>
        private readonly JsonNode _appDescriptor;

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
            foreach (var configuration in _appDescriptor["configurations"].AsArray().Where(c => c["dataType"].ToString() == SecretDataType.ToString()))
            {
                configuration["defaultValue"] = SecretMask;
            }

            return _appDescriptor;
        }

        /// <summary>
        /// Gets the secret configurations ids.
        /// </summary>
        /// <returns></returns>
        public List<string> GetSecretConfigurations()
        {
            return _appDescriptor["configurations"].AsArray().Where(c => c["dataType"].ToString() == SecretDataType.ToString()).Select(s => s["id"].ToString()).ToList();
        }
    }
}
