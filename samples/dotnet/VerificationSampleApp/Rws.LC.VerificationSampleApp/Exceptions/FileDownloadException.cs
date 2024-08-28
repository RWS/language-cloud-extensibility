using Microsoft.Extensions.Logging;
using Rws.LC.VerificationSampleApp.Exceptions;
using System.Net;

namespace Rws.LC.VerificationSampleApp.Exceptions
{
    public class FileDownloadException : AppException
    {
        public FileDownloadException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.FileDownloadException;
            StatusCode = HttpStatusCode.InternalServerError;
            ExceptionDetails = details;
        }    
    }
}
