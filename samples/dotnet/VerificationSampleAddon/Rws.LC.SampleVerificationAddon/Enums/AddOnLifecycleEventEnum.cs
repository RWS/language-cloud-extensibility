using System.Text.Json.Serialization;

namespace Rws.LC.SampleVerificationAddon.Enums
{
	public enum AddOnLifecycleEventEnum
	{
		/// <summary>
		/// Add-On was registered in LanguageCloud.
		/// </summary>
		REGISTERED,

		/// <summary>
		/// Add-On was unregistered in LanguageCloud.
		/// </summary>
		UNREGISTERED,

		/// <summary>
		/// Add-On was activated on a tenant account.
		/// </summary>
		ACTIVATED,

		/// <summary>
		/// Add-On was deactivated on a tenant account.
		/// </summary>
		DEACTIVATED
	}
}
