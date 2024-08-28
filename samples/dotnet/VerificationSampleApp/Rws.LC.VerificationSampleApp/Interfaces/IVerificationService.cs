using Rws.LC.VerificationSampleApp.Models;
using System.Collections.Generic;
using System.IO;
using System.Threading;
using System.Threading.Tasks;


namespace Rws.LC.VerificationSampleApp.Interfaces
{

    /// <summary>
    /// Used for verification
    /// </summary>
    public interface IVerificationService
    {
        /// <summary>
        /// Starts an asynchronous background job to verify a document. A callback will be made on the callback URL when this asynchronous job has completed
        /// </summary>
        /// <param name="request">request body for StartVerification</param>
        /// <param name="tenantId">tenant ID</param>
        /// <param name="traceId">trace ID</param>
        /// <returns></returns>
        Task<string> StartVerificationAsync(VerificationRequest request, string requestId, string tenantId, string traceId, CancellationToken cancellationToken);


        /// <summary>
        /// Verifies a list of BCM fragments
        /// </summary>
        /// <param name="request"></param>
        /// <returns>Response</returns>
        Task<VerifySegmentResponse> VerifySegment(VerifySegmentRequest request, CancellationToken cancellationToken);

        Task<MessageTypeResponse> GetMessagesByCulture(string culture, CancellationToken cancellationToken);

        Task<SettingsSchemaResponse> GetSchemas(CancellationToken cancellationToken);
    }
}
