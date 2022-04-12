namespace Rws.LC.AddonBlueprint.Models
{
    public class WorkflowConfiguration
    {
        /// <summary>
        /// The Id corresponding to an entry in the workflowTemplateConfiguration array from the addon descriptor
        /// </summary>
        public string Id { get; set; }

        /// <summary>
        /// The value assigned during workflow configuration
        /// </summary>
        public dynamic Value { get; set; }
    }
}