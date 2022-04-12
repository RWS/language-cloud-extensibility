using System.Collections.Generic;

namespace Rws.LC.AddonBlueprint.Models
{
    public class Action
    {
        /// <summary>
        /// The event
        /// </summary>
        public string Event { get; set; }

        /// <summary>
        /// The payload
        /// </summary>
        public List<string> Payload { get; set; }
    }
}
