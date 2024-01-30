using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.RestService.Interfaces
{
    /// <summary>
    /// Interface defining methods for FileManagement - this might be extended
    /// to a more complex implementation where multiple files are managed
    /// based on a unique ID, rather than just using filepaths
    /// </summary>
    public interface IFileManagementServiceClient
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="downloadUrl">one time download URL</param>
        /// <param name="requiredFileName">required filename for downloaded file</param>
        /// <returns></returns>
        public Task<string> DownloadFileAsync(string downloadUrl, string requiredFileName, CancellationToken token);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="uploadUrl">one time upload URL</param>
        /// <param name="sourceFilePath">path to sourcFile for uploading</param>
        /// <returns>resourceId</returns>
        public Task<string> UploadFileAsync(string uploadUrl, string sourceFilePath, CancellationToken token);
    }
}

