using Rws.LC.MTSampleApp.DAL.Entities;
using System.Threading.Tasks;

namespace Rws.LC.MTSampleApp.Interfaces
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
        /// Removes the app registration entity.
        /// </summary>
        /// <returns></returns>
        Task RemoveRegistrationInfo();
    }
}
