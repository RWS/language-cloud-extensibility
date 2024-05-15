using Rws.LC.AppBlueprint.Models;
using System.Collections.Generic;
using System.Linq;

namespace Rws.LC.AppBlueprint.DAL.Entities.Extensions
{
    public static class ModelExtensions
    {

        public static List<ConfigurationValueEntity> ToEntity(this List<ConfigurationValueModel> configurationValueModels)
        {
            return configurationValueModels.Select(config => config.ToEntity()).ToList();
        }

        public static ConfigurationValueEntity ToEntity(this ConfigurationValueModel configurationValueModel)
        {
            return new ConfigurationValueEntity
            {
                Id = configurationValueModel.Id,
                Value = configurationValueModel.Value
            };
        }

        public static ClientCredentialsEntity ToEntity(this ClientCredentials clientCredentials)
        {
            return new ClientCredentialsEntity
            {
                ClientId = clientCredentials.ClientId,
                ClientSecret = clientCredentials.ClientSecret
            };
        }
    }
}
