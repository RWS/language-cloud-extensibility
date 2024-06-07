using MongoDB.Bson.Serialization;
using MongoDB.Bson.Serialization.Serializers;
using System.Text.Json.Nodes;

namespace Rws.LC.VerificationSampleApp.DAL
{
    public class JsonNodeCustomSerializer : SerializerBase<JsonNode>
    {
        public override JsonNode Deserialize(BsonDeserializationContext context, BsonDeserializationArgs args)
        {
            string stringValue = BsonSerializer.Deserialize<string>(context.Reader);

            return JsonNode.Parse(stringValue);
        }
        public override void Serialize(BsonSerializationContext context, BsonSerializationArgs args, JsonNode value)
        {
            string stringValue = value.ToJsonString();
            BsonSerializer.Serialize(context.Writer, stringValue);
        }
    }
}
