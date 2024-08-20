using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Rws.LC.AppBlueprint.DAL.Entities
{
    [BsonIgnoreExtraElements]
    public class AppRegistrationEntity
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        [BsonIgnoreIfDefault]
        public string Id { get; set; }

        public string TenantId { get; set; }

        public string AppId { get; set; }

        /// <summary>
        /// The client credentials.
        /// </summary>
        public ClientCredentialsEntity ClientCredentials { get; set; }
    }
}
