using Microsoft.Extensions.Logging;
using Rws.LC.SampleVerificationAddon.Exceptions;
using Rws.VerificationSampleAddon.RestService.Exceptions;
using System.Collections.Generic;
using System.Net;

namespace Rws.LC.SampleVerificationAddon.RestService.Exceptions
{
    public class FileDownloadException : AddonException
    {
        public FileDownloadException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.FileDownloadException;
            StatusCode = HttpStatusCode.InternalServerError;
            ExceptionDetails = details;
        }    
    }
}
