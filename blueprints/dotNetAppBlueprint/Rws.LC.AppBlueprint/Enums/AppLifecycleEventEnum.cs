namespace Rws.LC.AppBlueprint.Enums
{
    public enum AppLifecycleEventEnum
    {
        /// <summary>
        /// App was registered in LanguageCloud.
        /// </summary>
        REGISTERED,

        /// <summary>
        /// App was unregistered in LanguageCloud.
        /// </summary>
        UNREGISTERED,

        /// <summary>
        /// App was activated on a tenant account.
        /// </summary>
        ACTIVATED,

        /// <summary>
        /// App was deactivated on a tenant account.
        /// </summary>
        DEACTIVATED
    }
}
