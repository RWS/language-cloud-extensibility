using System.Collections.Generic;

namespace Rws.LC.AddonBlueprint.Models
{
    public class AutomaticTaskRequestModel
    {
        /// <summary>
        /// The project id from LC.
        /// </summary>
        public string ProjectId { get; set; }

        /// <summary>
        /// The task id from LC.
        /// </summary>
        public string CorrelationId { get; set; }

        /// <summary>
        /// The workflow configuration settings for the current task.
        /// </summary>
        public List<WorkflowConfiguration> WorkflowConfiguration { get; set; }

        /// <summary>
        /// The callback Url used to return the task result.
        /// </summary>
        public string CallbackUrl { get; set; }

    }
}
