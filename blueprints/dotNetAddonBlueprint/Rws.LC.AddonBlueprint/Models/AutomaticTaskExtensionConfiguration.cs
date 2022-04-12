using System.Collections.Generic;

namespace Rws.LC.AddonBlueprint.Models
{
    public class AutomaticTaskExtensionConfiguration
    {
        /// <summary>
        /// The endpoints
        /// </summary>
        public AutomaticTaskEndpoints Endpoints { get; set; }

        /// <summary>
        /// The supported input filetypes.
        /// </summary>
        public string SupportedInputFileType { get; set; }

        /// <summary>
        /// The scope.
        /// </summary>
        public string Scope { get; set; }

        /// <summary>
        /// The output filetype.
        /// </summary>
        public string OutputFileType { get; set; }

        /// <summary>
        /// The outcomes.
        /// </summary>
        public List<Outcomes> Outcomes { get; set; }

        /// TODO: Model
        /// <summary>
        /// The workflow template configurations.
        /// </summary>
        public List<AddonConfigurationModel> WorkflowTemplateConfigurations { get; set; }
    }
}
