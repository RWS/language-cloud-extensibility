using System.Collections.Generic;

namespace Rws.LC.AddonBlueprint.Models
{
    public class UIExtensionsConfiguration
    {
        /// <summary>
        /// The elements
        /// </summary>
        public List<Element> Elements { get; set; }

        /// <summary>
        /// The initial requirements
        /// </summary>
        public string InitRequiremenets { get; set; }

        /// <summary>
        /// The script location
        /// </summary>
        public string ScriptPath { get; set; }
    }
}
