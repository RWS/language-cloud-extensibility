namespace Rws.LC.AddonBlueprint.Models
{
    public class Element
    {
        /// <summary>
        /// The location
        /// </summary>
        public string Location { get; set; }

        /// <summary>
        /// The action
        /// </summary>
        public Action Action { get; set; }

        /// <summary>
        /// The type
        /// Supported values: button
        /// </summary>
        public string Type { get; set; }

        /// <summary>
        /// The icon from the Font Awesom library, that will be displayed on the control.
        /// For example if it's on type "button"
        /// </summary>
        public string Icon { get; set; }

        /// <summary>
        /// The text that will be displayed on the control.
        /// For example if it's of type "button"
        /// </summary>
        public string Text { get; set; }
    }
}
