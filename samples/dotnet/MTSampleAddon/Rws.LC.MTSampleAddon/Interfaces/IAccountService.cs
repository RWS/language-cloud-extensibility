﻿using Rws.LC.MTSampleAddon.Models;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.MTSampleAddon.Interfaces
{
    /// <summary>
    /// Defines a service that allows creation and manipulation of account related data.
    /// </summary>
    public interface IAccountService
    {
        /// <summary>
        /// Saves the account information.
        /// </summary>
        /// <param name="activatedEvent">The activated event.</param>
        /// <param name="tenantId">The tenant identifier.</param>
        /// <param name="cancellationToken">The cancellation token.</param>
        Task SaveAccountInfo(ActivatedEvent activatedEvent, string tenantId, CancellationToken cancellationToken);

        /// <summary>
        /// Removes the account information.
        /// </summary>
        /// <param name="tenantId">The tenant id.</param>
        /// <param name="cancellationToken">The cancellation token.</param>
        Task RemoveAccountInfo(string tenantId, CancellationToken cancellationToken);

        /// <summary>
        /// Removes all the tenant related information.
        /// </summary>
        /// <param name="cancellationToken"></param>
        Task RemoveAccounts(CancellationToken cancellationToken);

        /// <summary>
        /// Saves or updates the configuration settings
        /// </summary>
        /// <param name="tenantId">The tenant id.</param>
        /// <param name="configurationValues">The configuration values.</param>
        /// <param name="cancellationToken">The cancellation token.</param>
        /// <returns>The updated configuration settings result.</returns>
        Task<ConfigurationSettingsResult> SaveOrUpdateConfigurationSettings(string tenantId, List<ConfigurationValueModel> configurationValues, CancellationToken cancellationToken);

        /// <summary>
        /// Gets the configuration settings.
        /// </summary>
        /// <param name="tenantId">The tenant id.</param>
        /// <param name="cancellationToken">The cancellation token.</param>
        /// <returns>The configuration settings result.</returns>
        Task<ConfigurationSettingsResult> GetConfigurationSettings(string tenantId, CancellationToken cancellationToken);

        /// <summary>
        /// Validates the configuration settings.
        /// </summary>
        /// <param name="tenantId">The tenant id.</param>
        /// <param name="cancellationToken">The cancellation token.</param>
        Task ValidateConfigurationSettings(string tenantId, CancellationToken cancellationToken);
    }
}
