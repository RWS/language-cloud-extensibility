﻿using Rws.LC.AppBlueprint.Models;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.AppBlueprint.Interfaces
{
    /// <summary>
    /// Defines a service that allows creation and manipulation of account related data.
    /// </summary>
    public interface IAccountService
    {
        /// <summary>
        /// Saves the registration information.
        /// </summary>
        /// <param name="registeredEvent">The registered event information.</param>
        /// <param name="tenantId">The LC tenant id.</param>
        /// <param name="appId">The app registration id from LC.</param>
        /// <param name="cancellationToken">The cancellation token.</param>
        /// <returns></returns>
        Task SaveRegistrationInfo(RegisteredEvent registeredEvent, string tenantId, string appId, CancellationToken cancellationToken);

        /// <summary>
        /// Removes the registration information.
        /// </summary>
        /// <param name="cancellationToken">The cancellation token.</param>
        /// <returns></returns>
        Task RemoveRegistrationInfo(CancellationToken cancellationToken);

        /// <summary>
        /// Saves the account information.
        /// </summary>
        /// <param name="tenantId">The tenant identifier.</param>
        /// <param name="region">The tenant region.</param>
        /// <param name="cancellationToken">The cancellation token.</param>
        Task SaveAccountInfo(string tenantId, string region, CancellationToken cancellationToken);

        /// <summary>
        /// Removes the account information.
        /// </summary>
        /// <param name="tenantId">The tenant identifier.</param>
        /// <param name="cancellationToken">The cancellation token.</param>
        Task RemoveAccountInfo(string tenantId, CancellationToken cancellationToken);

        /// <summary>
        /// Removes all the tenant related information.
        /// </summary>
        /// <param name="cancellationToken"></param>
        Task RemoveAccounts(CancellationToken cancellationToken);

        /// <summary>
        /// Validates the lifecycle event corresponds to the tenant and app id.
        /// </summary>
        /// <param name="tenantId">The tenant id.</param>
        /// <param name="appId">The app id.</param>
        /// <returns></returns>
        Task ValidateLifecycleEvent(string tenantId, string appId);

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
