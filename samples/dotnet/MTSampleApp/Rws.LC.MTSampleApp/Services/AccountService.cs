using Rws.LC.MTSampleApp.DAL.Entities;
using Rws.LC.MTSampleApp.DAL.Entities.Extensions;
using Rws.LC.MTSampleApp.Exceptions;
using Rws.LC.MTSampleApp.Interfaces;
using Rws.LC.MTSampleApp.Models;
using Rws.LC.MTSampleApp.Models.Extensions;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.MTSampleApp.Services
{
    /// <summary>
    /// Defines a service that allows creation and manipulation of account related data.
    /// </summary>
    public class AccountService : IAccountService
    {
        public AccountService(IRepository repository, IAppRegistrationRepository appRegistrationRepository, IDescriptorService descriptorService)
        {
            _repository = repository;
            _appRegistrationRepository = appRegistrationRepository;
            _descriptorService = descriptorService;
        }

        private IRepository _repository;
        private readonly IAppRegistrationRepository _appRegistrationRepository;
        private IDescriptorService _descriptorService;

        /// <summary>
        /// The const string to be used for masking secrets.
        /// </summary>
        private const string SecretMask = "*****";

        /// <inheritdoc/>
        public async Task SaveRegistrationInfo(RegisteredEvent registeredEvent, CancellationToken cancellationToken)
        {
            if (string.IsNullOrEmpty(registeredEvent.ClientCredentials?.ClientId))
            {
                throw new AppValidationException($"Invalid {nameof(registeredEvent.ClientCredentials.ClientId)} provided.", new Details { Code = ErrorCodes.InvalidInput, Name = nameof(registeredEvent.ClientCredentials.ClientId), Value = null });
            }

            if (string.IsNullOrEmpty(registeredEvent.ClientCredentials?.ClientSecret))
            {
                throw new AppValidationException($"Invalid {nameof(registeredEvent.ClientCredentials.ClientSecret)} provided.", new Details { Code = ErrorCodes.InvalidInput, Name = nameof(registeredEvent.ClientCredentials.ClientSecret), Value = null });
            }

            AppRegistrationEntity appRegistrationEntity = new AppRegistrationEntity()
            {
                ClientCredentials = registeredEvent.ClientCredentials.ToEntity()
            };

            var appRegistration = await _appRegistrationRepository.GetRegistrationInfo().ConfigureAwait(false);
            if (appRegistration != null)
            {
                throw new DoubleRegistrationException();
            }

            await _appRegistrationRepository.SaveRegistrationInfo(appRegistrationEntity).ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task RemoveRegistrationInfo(CancellationToken cancellationToken)
        {
            await _appRegistrationRepository.RemoveRegistrationInfo().ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task SaveAccountInfo(string tenantId, string region, CancellationToken cancellationToken)
        {
            if (string.IsNullOrEmpty(tenantId))
            {
                throw new AppValidationException($"Invalid {nameof(tenantId)} provided.", new Details { Code = ErrorCodes.InvalidInput, Name = nameof(tenantId), Value = null });
            }

            AccountInfoEntity accountInfoEntity = new AccountInfoEntity()
            {
                TenantId = tenantId,
                Region = region
            };

            var accountInfo = await _repository.GetAccountInfoByTenantId(tenantId).ConfigureAwait(false);
            if (accountInfo != null)
            {
                return;
            }

            // sensitive information such as the service account key(SAMPLE_ACCOUNT_SECRET) should be encrypted
            // https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/csfle/#mongodb-crypt
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
                throw new AccountValidationException($"Account {tenantId} is not installed!", new Details { Code = ErrorCodes.AccountNotInstalled, Name = nameof(tenantId), Value = tenantId });
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
                throw new AccountValidationException($"Account {tenantId} is not installed!", new Details { Code = ErrorCodes.AccountNotInstalled, Name = nameof(tenantId), Value = tenantId });
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
                throw new AccountValidationException($"Account {tenantId} is not installed!", new Details { Code = ErrorCodes.AccountNotInstalled, Name = nameof(tenantId), Value = tenantId });
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
