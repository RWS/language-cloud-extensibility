using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using Rws.LC.MTSampleAddon.Enums;
using Rws.LC.MTSampleAddon.Interfaces;
using Rws.LC.MTSampleAddon.Models;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace Rws.LC.MTSampleAddon.Services
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
            _addonDescriptor = JsonConvert.DeserializeObject<AddonDescriptorModel>(descriptorText);
        }

        /// <summary>
        /// The addon descriptor.
        /// </summary>
        private readonly AddonDescriptorModel _addonDescriptor;

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
        public AddonDescriptorModel GetDescriptor()
        {
            foreach (var configuration in _addonDescriptor.Configurations.Where(c => c.DataType == SecretDataType))
            {
                configuration.DefaultValue = SecretMask;
            }

            return _addonDescriptor;
        }

        /// <summary>
        /// Gets the secret configurations ids.
        /// </summary>
        /// <returns></returns>
        public List<string> GetSecretConfigurations()
        {
            return _addonDescriptor.Configurations.Where(c => c.DataType == SecretDataType).Select(s => s.Id).ToList();
        }
    }
}
