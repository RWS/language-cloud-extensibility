using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using Sdl.ApiClientSdk.Core.Exceptions;
using Sdl.ApiClientSdk.Core.Models;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.AddonSample.Preview.Helpers
{
    public class RestClientBase
    {
        protected HttpClient httpClient;

        JsonSerializerSettings JsonSerializerSettings = new JsonSerializerSettings()
        {
            ContractResolver = new CamelCasePropertyNamesContractResolver(),
            NullValueHandling = NullValueHandling.Ignore
        };

        public RestClientBase()
        {
            httpClient = new HttpClient();
        }

        protected async Task<HttpResponseMessage> SendAsync(string url, HttpMethod httpMethod, object body, Dictionary<string, string> headers, CancellationToken cancellationToken)
        {
            HttpRequestMessage request = new HttpRequestMessage(httpMethod, url);

            if (body != null)
            {   
                if (body is MultipartFormDataContent multipartBody)
                {
                    request.Content = multipartBody;
                }
                else
                {
                    string jsonContent = SerializeContent(body);
                    var content = new StringContent(jsonContent, new UTF8Encoding(), "application/json");
                    request.Content = content;
                }
            }

            HttpResponseMessage result;

            if(headers != null)
            {
                foreach(var key in headers.Keys)
                {
                    request.Headers.Add(key, headers[key]);
                }
            }

            try
            {
                result = await httpClient.SendAsync(request, cancellationToken).ConfigureAwait(false);
            }
            catch (HttpRequestException ex)
            {
                throw new ApiConnectionException(url, httpMethod, ex);
            }

            if (result.StatusCode == System.Net.HttpStatusCode.Unauthorized)
            {
                var error = await ExtractErrorDetails(result);
                throw new ApiUnauthorizedException(request.RequestUri.ToString(), request.Method, result.StatusCode, error.errorResult);
            }

            if (result.StatusCode == System.Net.HttpStatusCode.Forbidden)
            {
                var error = await ExtractErrorDetails(result);
                throw new ApiForbiddenException(request.RequestUri.ToString(), request.Method, result.StatusCode, error.errorResult);
            }

            if (!result.IsSuccessStatusCode)
            {
                var error = await ExtractErrorDetails(result);
                throw new ApiErrorException(error.errorDetails ?? new ApiErrorResponse(), result.StatusCode, url, request.Method, error.errorResult);
            }

            return result;
        }

        private async Task<(ApiErrorResponse errorDetails, string errorResult)> ExtractErrorDetails(HttpResponseMessage result)
        {
            ApiErrorResponse errorDetails = null;
            string errorResult = null;
            if (result.Content != null)
            {
                errorResult = await result.Content.ReadAsStringAsync();
                errorDetails = Newtonsoft.Json.JsonConvert.DeserializeObject<ApiErrorResponse>(errorResult, new Newtonsoft.Json.JsonSerializerSettings
                {
                    Error = (a, b) =>
                    {
                        b.ErrorContext.Handled = true;
                    }
                });
            }

            return (errorDetails, errorResult);
        }

        private string SerializeContent(object content)
        {
            var jsonContent = Newtonsoft.Json.JsonConvert.SerializeObject(content, JsonSerializerSettings);
            return jsonContent;
        }


        private static string BuildUrl(string extensionUrl, string endpoint)
        {
            return extensionUrl.TrimEnd('/') + '/' + endpoint.TrimStart('/');
        }
    }
}
