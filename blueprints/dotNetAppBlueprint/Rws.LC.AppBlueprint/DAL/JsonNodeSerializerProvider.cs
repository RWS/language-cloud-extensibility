using MongoDB.Bson.Serialization;
using System;
using System.Text.Json.Nodes;

namespace Rws.LC.AppBlueprint.DAL
{
    public class JsonNodeSerializerProvider : IBsonSerializationProvider
    {
        public IBsonSerializer GetSerializer(Type type)
        {
            return type == typeof(JsonNode) ? new JsonNodeCustomSerializer() : null;
        }
    }
}
