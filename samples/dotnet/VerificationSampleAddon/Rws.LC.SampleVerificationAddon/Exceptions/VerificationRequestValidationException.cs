using Microsoft.Extensions.Logging;
using Rws.LC.SampleVerificationAddon.Exceptions;
using Rws.VerificationSampleAddon.RestService.Exceptions;
using System.Net;

namespace Rws.LC.SampleVerificationAddon.RestService.Exceptions
{
    public class VerificationRequestValidationException : AddonException
    {
        // Example exception for verification request validation failure
        public VerificationRequestValidationException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.VerificationException;
            StatusCode = HttpStatusCode.Unauthorized;
            ExceptionDetails = details;
        }
    }
}
