using Rws.LC.VerificationSampleApp.Exceptions;
using System.Net;

namespace Rws.LC.VerificationSampleApp.Exceptions
{
    public class FileUploadException : AppException
    {
        public FileUploadException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.FileUploadException;
            StatusCode = HttpStatusCode.InternalServerError;
            ExceptionDetails = details;
        }
    }
}
