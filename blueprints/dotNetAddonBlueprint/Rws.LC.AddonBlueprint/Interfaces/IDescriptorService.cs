using Rws.LC.AddonBlueprint.Models;
using System.Collections.Generic;

namespace Rws.LC.AddonBlueprint.Interfaces
{
    /// <summary>
    /// Used to return the addon descriptor
    /// </summary>
    public interface IDescriptorService
    {
        /// <summary>
        /// Gets the descriptor.
        /// </summary>
        /// <returns></returns>
        AddonDescriptorModel GetDescriptor();

        /// <summary>
        /// Gets the secret configurations ids.
        /// </summary>
        /// <returns></returns>
        List<string> GetSecretConfigurations();
    }
}
