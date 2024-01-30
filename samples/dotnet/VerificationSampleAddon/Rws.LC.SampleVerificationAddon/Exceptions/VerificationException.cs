using Microsoft.Extensions.Logging;
using Rws.LC.SampleVerificationAddon.Exceptions;
using Rws.VerificationSampleAddon.RestService.Exceptions;
using System.Net;

namespace Rws.LC.SampleVerificationAddon.RestService.Exceptions
{
    public class VerificationException : AddonException
    {
        // Example exception for verification - this needs to be more granular depending on the actual error
        public VerificationException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.VerificationException;
            StatusCode = HttpStatusCode.Unauthorized;
            ExceptionDetails = details;
        }
    }
}
