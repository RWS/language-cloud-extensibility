using MongoDB.Bson.IO;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Serialization;
using Rws.LC.VerificationSampleApp.Exceptions;
using Rws.LC.VerificationSampleApp.Interfaces;
using System;
using System.Globalization;
using System.IO;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using Rws.LC.AddonSample.Preview.Helpers;
using Rws.LC.VerificationSampleApp.Exceptions;

namespace Rws.LC.VerificationSampleApp.Services
{
    public class FileManagementServiceClient : RestClientBase, IFileManagementServiceClient
    {

        JsonSerializerSettings JsonSerializerSettings = new JsonSerializerSettings()
        {
            ContractResolver = new CamelCasePropertyNamesContractResolver(),
            NullValueHandling = NullValueHandling.Ignore
        };



        public async Task<string> UploadFileAsync(string uploadUrl, string sourceFilePath, CancellationToken token)
        {
            try
            {

                MultipartFormDataContent GetMultipartFormDataContent()
                {
                    var multipartContent = new MultipartFormDataContent("Upload----" + DateTime.Now.ToString(CultureInfo.InvariantCulture));

                    return multipartContent;
                }

                using (var multipartContent = GetMultipartFormDataContent())
                {
                    var fileName = Path.GetFileName(sourceFilePath);

                    using (var fileStream = File.OpenRead(sourceFilePath))
                    {

                        multipartContent.Add(new StreamContent(fileStream), "file", fileName);

                        var response = await SendAsync(uploadUrl, HttpMethod.Post, multipartContent, null, token);

                        var responseString = await response.Content.ReadAsStringAsync().ConfigureAwait(false);

                        var fileId = JObject.Parse(responseString)["id"].Value<string>();

                        return fileId;
                    }
                }

            }
            catch (Exception ex)
            {
                throw new FileUploadException("Error when uploading file", new Details {Code = "AEM.Job.Error.FileUploadError", Name = "Dynamic Job", Value = ex.Message } );
            }
        }

        async public Task<string> DownloadFileAsync(string downloadUrl, string requiredFileName, CancellationToken token)
        {
            try
            {
                var response = await SendAsync(downloadUrl, HttpMethod.Get, null, null, token);

                var filePath = Path.GetTempPath();
                var fileName = Path.Combine(filePath, requiredFileName);
                if (!response.IsSuccessStatusCode)
                {
                    throw new FileDownloadException("Error when downloading file", new Details { Code = "Dynamic.Job.Error.FileDownloadError", Name = "Dynamic Job", Value = response.StatusCode.ToString() });
                }

                using (response)
                using (var streamToReadFrom = await response.Content.ReadAsStreamAsync().ConfigureAwait(false))
                {
                    using (var fileStream = new FileStream(fileName, FileMode.OpenOrCreate))
                    {
                        await streamToReadFrom.CopyToAsync(fileStream);
                    }
                }

                return fileName;
            }
            catch(Exception ex)
            {
                throw new FileDownloadException("Error when downloading file", new Details { Code = "Dynamic.Job.Error.FileDownloadError", Name = "Dynamic Job", Value = ex.Message });
            }
        }

    }
}
