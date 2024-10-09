using Rws.LC.UISampleApp.Enums;

namespace Rws.LC.UISampleApp.Models
{
    public class AppLifecycleEvent<T> : AppLifecycleEvent where T : class
    {
        /// <summary>
        /// The data object.
        /// </summary>
        public T Data { get; set; }
    }

    public class AppLifecycleEvent
    {
        /// <summary>
        /// The app lifecycle event id.
        /// </summary>
        public AppLifecycleEventEnum Id { get; set; }

        /// <summary>
        /// The timestamp.
        /// </summary>
        public string Timestamp { get; set; }
    }
}
