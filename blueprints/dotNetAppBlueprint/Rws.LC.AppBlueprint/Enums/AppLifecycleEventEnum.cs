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
        /// App was installed on a tenant account.
        /// </summary>
        INSTALLED,

        /// <summary>
        /// App was uninstalled on a tenant account.
        /// </summary>
        UNINSTALLED
    }
}
