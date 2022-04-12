﻿namespace Rws.LC.AddonBlueprint.Models
{
    public class AddonStandardEndpointsModel
    {
        /// <summary>
        /// The health endpoint.
        /// </summary>
        public string Health { get; set; }

        /// <summary>
        /// The documentation endpoint.
        /// </summary>
        public string Documentation { get; set; }

        /// <summary>
        /// The add-on lifecycle endpoint.
        /// </summary>
        public string AddonLifecycle { get; set; }

        /// <summary>
        /// The configuration endpoint.
        /// </summary>
        public string Configuration { get; set; }

        /// <summary>
        /// The configuration validation endpoint.
        /// </summary>
        public string ConfigurationValidation { get; set; }

        /// <summary>
        /// The icon endpoint.
        /// </summary>
        public string Icon { get; set; }
    }
}
