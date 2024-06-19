using System.Text.Json;
using System.Text.Json.Serialization;

namespace Rws.LC.UISampleApp.Helpers
{
    /// <summary>
    /// Helper class for Json Serializer Options
    /// </summary>
    public class JsonSettings
    {
        /// <summary>
        /// Gets the default serializer options
        /// </summary>
        /// <returns></returns>
        public static JsonSerializerOptions Default()
        {
            var options = new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true,
                DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull,
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            };
            options.Converters.Add(new JsonStringEnumConverter());

            return options;
        }
    }
}
