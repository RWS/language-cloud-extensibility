using Rws.LC.SampleVerificationAddon.DAL.Entities;
using System.Collections.Generic;
using System.Linq;

namespace Rws.LC.SampleVerificationAddon.Models.Extensions
{
    public static class EntityExtensions
    {
        public static List<ConfigurationValueModel> ToModel(this List<ConfigurationValueEntity> configurationValueEntities)
        {
            return configurationValueEntities.Select(config => config.ToModel()).ToList();
        }

        private static ConfigurationValueModel ToModel(this ConfigurationValueEntity configurationValueEntity)
        {
            return new ConfigurationValueModel
            {
                Id = configurationValueEntity.Id,
                Value = configurationValueEntity.Value
            };
        }
    }
}
