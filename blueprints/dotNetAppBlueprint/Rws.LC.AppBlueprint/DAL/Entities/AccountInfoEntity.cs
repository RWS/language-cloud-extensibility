﻿using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using System.Collections.Generic;

namespace Rws.LC.AppBlueprint.DAL.Entities
{
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
    }
}

