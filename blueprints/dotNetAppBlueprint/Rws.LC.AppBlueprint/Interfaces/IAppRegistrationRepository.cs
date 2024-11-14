using Rws.LC.AppBlueprint.DAL.Entities;
using System.Threading.Tasks;

namespace Rws.LC.AppBlueprint.Interfaces
{
    public interface IAppRegistrationRepository
    {
        /// <summary>
        /// Saves the app registration entity.
        /// </summary>
        /// <param name="entity">The app registration entity.</param>
        /// <returns></returns>
        Task SaveRegistrationInfo(AppRegistrationEntity entity);

        /// <summary>
        /// Retrieves the app registration entity.
        /// </summary>
        /// <returns>The app registration entity</returns>
        Task<AppRegistrationEntity> GetRegistrationInfo();

        /// <summary>
        /// Retrieves the app registration entity.
        /// </summary>
        /// <param name="tenantId">The tenant id.</param>
        /// <param name="appId">The app id.</param>
        /// <returns>The app registration entity</returns>
        Task<AppRegistrationEntity> GetRegistrationInfo(string tenantId, string appId);

        /// <summary>
        /// Removes the app registration entity.
        /// </summary>
        /// <returns></returns>
        Task RemoveRegistrationInfo();
    }
}
