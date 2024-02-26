using System.Text.Json.Nodes;

namespace Rws.LC.AppBlueprint.Models
{
    public class WorkflowConfiguration
    {
        /// <summary>
        /// The Id corresponding to an entry in the workflowTemplateConfiguration array from the app descriptor
        /// </summary>
        public string Id { get; set; }

        /// <summary>
        /// The value assigned during workflow configuration
        /// </summary>
        public JsonNode Value { get; set; }
    }
}