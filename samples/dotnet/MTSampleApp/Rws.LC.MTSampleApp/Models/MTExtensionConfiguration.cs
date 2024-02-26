namespace Rws.LC.MTSampleApp.Models
{
    public class MTExtensionConfiguration
    {
        /// <summary>
        /// The endpoints
        /// </summary>
        public MTEndpoints Endpoints { get; set; }

        /// <summary>
        /// The document format
        /// html or bcm
        /// </summary>
        public string Format { get; set; }
    }
}