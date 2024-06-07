using Rws.LC.VerificationSampleApp.Exceptions;
using System.Net;

namespace Rws.LC.VerificationSampleApp.Exceptions
{
    public class VerificationRequestValidationException : AppException
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
