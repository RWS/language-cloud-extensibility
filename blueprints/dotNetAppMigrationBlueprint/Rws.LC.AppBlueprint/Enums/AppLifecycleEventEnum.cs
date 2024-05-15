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
        /// App was activated on a tenant account. This is kept for backwards compatibility for the addons that upgrade from descriptorVersion 1.3 to 1.4
        /// </summary>
        ACTIVATED,

        /// <summary>
        /// App was installed(formerly activated) on a tenant account.
        /// </summary>
        INSTALLED,

        /// <summary>
        /// App version 1.4 was detected in LanguageCloud.
        /// </summary>
        UPDATED,

        /// <summary>
        /// App was deactivated on a tenant account. This is kept for backwards compatibility for the addons that upgrade from descriptorVersion 1.3 to 1.4
        /// </summary>
        DEACTIVATED,

        /// <summary>
        /// App was uninstalled(formerly deactivated) on a tenant account.
        /// </summary>
        UNINSTALLED
    }
}
