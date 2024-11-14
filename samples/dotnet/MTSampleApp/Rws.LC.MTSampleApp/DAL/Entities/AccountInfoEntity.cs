using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using Rws.LC.MTSampleApp.Helpers;
using System;
using System.Collections.Generic;
using System.Linq;

namespace Rws.LC.MTSampleApp.DAL.Entities
{
    [BsonIgnoreExtraElements]
    public class AccountInfoEntity
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string Id { get; set; }

        /// <summary>
        /// The tenant id.
        /// </summary>
        public string TenantId { get; set; }

        /// <summary>
        /// The region of the tenant.
        /// </summary>
        public string Region { get; set; }

        /// <summary>
        /// The configuration values.
        /// </summary>
        public List<ConfigurationValueEntity> ConfigurationValues { get; set; }

        public string GetServiceKey()
        {
            return Convert.ToString(ConfigurationValues.Where(c => c.Id.Equals(Constants.GoogleServiceKey)).First().Value);
        }

        public string GetProjectId()
        {
            return Convert.ToString(ConfigurationValues.Where(c => c.Id.Equals(Constants.GoogleProjectId)).First().Value);
        }
    }
}

