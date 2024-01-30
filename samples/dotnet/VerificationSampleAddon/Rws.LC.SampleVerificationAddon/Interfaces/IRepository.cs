using Rws.LC.SampleVerificationAddon.DAL.Entities;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.Interfaces
{
    public interface IRepository
    {
        /// <summary>
        /// Saves the account info entity.
        /// </summary>
        /// <param name="entity">The account info entity.</param>
        /// <returns></returns>
        Task SaveAccount(AccountInfoEntity accountInfoEntity);

        /// <summary>
        /// Gets the account info entity by tenant id.
        /// </summary>
        /// <param name="tenantId">The tenant id.</param>
        /// <returns>The account info entity.</returns>
        Task<AccountInfoEntity> GetAccountInfoByTenantId(string tenantId);

        /// <summary>
        /// Removes the account by tenant id.
        /// </summary>
        /// <param name="tenantId">The tenant id.</param>
        /// <returns></returns>
        Task RemoveAccount(string tenantId);

        /// <summary>
        /// Removes all the accounts.
        /// </summary>
        /// <returns></returns>
        Task RemoveAccounts();

        /// <summary>
        /// Saves or updates the configurations settings.
        /// </summary>
        /// <param name="accountInfoEntity">The account info entity.</param>
        /// <returns>The updated account info entity.</returns>
        Task<AccountInfoEntity> SaveOrUpdateConfigurationSettings(AccountInfoEntity accountInfoEntity);
    }
}
