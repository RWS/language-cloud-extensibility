using Rws.LC.SampleVerificationAddon.DAL;
using Rws.LC.SampleVerificationAddon.DAL.Entities;
using Rws.LC.SampleVerificationAddon.DAL.Entities.Extensions;
using Rws.LC.SampleVerificationAddon.Exceptions;
using Rws.LC.SampleVerificationAddon.Interfaces;
using Rws.LC.SampleVerificationAddon.Models;
using Rws.LC.SampleVerificationAddon.Models.Extensions;
using Rws.VerificationSampleAddon.RestService.Exceptions;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.Services
{
    /// <summary>
    /// Defines a service that allows creation and manipulation of account related data.
    /// </summary>
    public class AccountService : IAccountService
    {
        public AccountService(IRepository repository, IDescriptorService descriptorService)
        {
            _repository = repository;
            _descriptorService = descriptorService;
        }

        private IRepository _repository;

        private IDescriptorService _descriptorService;

        /// <summary>
        /// The const string to be used for masking secrets.
        /// </summary>
        private const string SecretMask = "*****";

        /// <inheritdoc/>
        public async Task SaveAccountInfo(ActivatedEvent activatedEvent, string tenantId, CancellationToken cancellationToken)
        {
            if (string.IsNullOrEmpty(tenantId))
            {
                throw new AddonValidationException($"Invalid {nameof(tenantId)} provided.", new Details { Code = ErrorCodes.InvalidInput, Name = nameof(tenantId), Value = null });
            }

            if (string.IsNullOrEmpty(activatedEvent.ClientCredentials?.ClientId))
            {
                throw new AddonValidationException($"Invalid {nameof(activatedEvent.ClientCredentials.ClientId)} provided.", new Details { Code = ErrorCodes.InvalidInput, Name = nameof(activatedEvent.ClientCredentials.ClientId), Value = null });
            }

            if (string.IsNullOrEmpty(activatedEvent.ClientCredentials?.ClientSecret))
            {
                throw new AddonValidationException($"Invalid {nameof(activatedEvent.ClientCredentials.ClientSecret)} provided.", new Details { Code = ErrorCodes.InvalidInput, Name = nameof(activatedEvent.ClientCredentials.ClientSecret), Value = null });
            }

            AccountInfoEntity accountInfoEntity = new AccountInfoEntity()
            {
                TenantId = tenantId,
                ClientCredentials = activatedEvent.ClientCredentials.ToEntity()
            };

            var accountInfo = await _repository.GetAccountInfoByTenantId(tenantId).ConfigureAwait(false);
            if (accountInfo != null)
            {
                return;
            }

            await _repository.SaveAccount(accountInfoEntity).ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task RemoveAccountInfo(string tenantId, CancellationToken cancellationToken)
        {
            await _repository.RemoveAccount(tenantId).ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task RemoveAccounts(CancellationToken cancellationToken)
        {
            await _repository.RemoveAccounts().ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task<ConfigurationSettingsResult> GetConfigurationSettings(string tenantId, CancellationToken cancellationToken)
        {
            var accountInfo = await _repository.GetAccountInfoByTenantId(tenantId).ConfigureAwait(false);
            if (accountInfo == null)
            {
                throw new AccountValidationException($"Account {tenantId} is not activated!", null, new Details{  Code = ErrorCodes.AccountNotActivated, Name = nameof(tenantId), Value = tenantId } );
            }

            if (accountInfo.ConfigurationValues == null)
            {
                return new ConfigurationSettingsResult(new List<ConfigurationValueModel>());
            }

            return MaskSecretConfigurations(accountInfo.ConfigurationValues);
        }

        /// <inheritdoc/>
        public async Task<ConfigurationSettingsResult> SaveOrUpdateConfigurationSettings(string tenantId, List<ConfigurationValueModel> configurationValues, CancellationToken cancellationToken)
        {
            AccountInfoEntity accountInfo = await _repository.GetAccountInfoByTenantId(tenantId).ConfigureAwait(false);
            if (accountInfo == null)
            {
                throw new AccountValidationException($"Account {tenantId} is not activated!", null, new Details { Code = ErrorCodes.AccountNotActivated, Name = nameof(tenantId), Value = tenantId });
            }

            accountInfo = UpdateConfigurationsForAccount(accountInfo, configurationValues);
            var updatedAccountInfoEntity = await _repository.SaveOrUpdateConfigurationSettings(accountInfo).ConfigureAwait(false);

            return MaskSecretConfigurations(updatedAccountInfoEntity.ConfigurationValues);
        }

        /// <inheritdoc/>
        public async Task ValidateConfigurationSettings(string tenantId, CancellationToken cancellationToken)
        {
            var accountInfo = await _repository.GetAccountInfoByTenantId(tenantId).ConfigureAwait(false);
            if (accountInfo == null)
            {
                throw new AccountValidationException($"Account {tenantId} is not activated!", null, new Details { Code = ErrorCodes.AccountNotActivated, Name = nameof(tenantId), Value = tenantId });
            }

            var configurations = accountInfo.ConfigurationValues;
            VerifyNoInvalidValues(configurations);
            VerifyNoInvalidKeys(configurations);
            VerifyNoNullValues(configurations);
            VerifySetup();
        }

        /// <summary>
        /// Updates the configurations for an account.
        /// </summary>
        /// <param name="accountInfo">The account information.</param>
        /// <param name="configurationValues">The configuration values.</param>
        /// <returns>The updated account info entity.</returns>
        private AccountInfoEntity UpdateConfigurationsForAccount(AccountInfoEntity accountInfo, List<ConfigurationValueModel> configurationValues)
        {
            if (accountInfo.ConfigurationValues == null)
            {
                accountInfo.ConfigurationValues = configurationValues.ToEntity();
            }
            else
            {
                foreach (var config in configurationValues)
                {
                    var matchedItem = accountInfo.ConfigurationValues.FirstOrDefault(f => f.Id == config.Id);
                    if (matchedItem == null)
                    {
                        accountInfo.ConfigurationValues.Add(config.ToEntity());
                    }
                    else
                    {
                        matchedItem.Value = config.Value;
                    }
                }
            }

            return accountInfo;
        }



        /// <summary>
        /// Masks the secret configuration values.
        /// </summary>
        /// <param name="configurations">A list of configurations</param>
        /// <returns></returns>
        private ConfigurationSettingsResult MaskSecretConfigurations(List<ConfigurationValueEntity> configurations)
        {
            var secretConfigurationIds = _descriptorService.GetSecretConfigurations();

            foreach (var config in configurations.Where(config => secretConfigurationIds.Contains(config.Id)))
            {
                if (config.Value != null)
                {
                    config.Value = SecretMask;
                }
            }

            return new ConfigurationSettingsResult(configurations.ToModel());
        }

        private void VerifyNoInvalidValues(List<ConfigurationValueEntity> configurations)
        {
            List<Details> errorDetails = new List<Details>();
            //Details errorDetail = new Details() { Name = "name", Code = ErrorCodes.InvalidValue, Value = "value" };
            //errorDetails.Add(errorDetail);

            if (errorDetails.Any())
            {
                throw new ConfigurationValidationException(ErrorMessages.InvalidValueMessage, errorDetails.ToArray());
            }
        }

        private void VerifyNoInvalidKeys(List<ConfigurationValueEntity> configurations)
        {
            List<Details> errorDetails = new List<Details>();
            //Details errorDetail = new Details() { Name = "name", Code = ErrorCodes.InvalidKey, Value = "key" };
            //errorDetails.Add(errorDetail);

            if (errorDetails.Any())
            {
                throw new ConfigurationValidationException(ErrorMessages.InvalidKeyMessage, errorDetails.ToArray());
            }
        }

        private void VerifyNoNullValues(List<ConfigurationValueEntity> configurations)
        {
            List<Details> errorDetails = new List<Details>();
            //Details errorDetail = new Details() { Name = "name", Code = ErrorCodes.NullValue, Value = null };
            //errorDetails.Add(errorDetail);

            if (errorDetails.Any())
            {
                throw new ConfigurationValidationException(ErrorMessages.NullValueMessage, errorDetails.ToArray());
            }
        }

        private void VerifySetup()
        {
            //how to send an error exemple:
            //throw new SetupValidationException(ErrorMessages.InvalidSetupMessage, new Details[] { });
        }
    }
}
