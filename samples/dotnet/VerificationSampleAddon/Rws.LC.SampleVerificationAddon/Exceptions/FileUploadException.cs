using Microsoft.Extensions.Logging;
using Rws.LC.SampleVerificationAddon.Exceptions;
using Rws.VerificationSampleAddon.RestService.Exceptions;
using System.Net;

namespace Rws.LC.SampleVerificationAddon.RestService.Exceptions
{
    public class FileUploadException : AddonException
    {
        public FileUploadException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.FileUploadException;
            StatusCode = HttpStatusCode.InternalServerError;
            ExceptionDetails = details;
        }
    }
}
