using Rws.LC.VerificationSampleApp.Exceptions;
using System.Net;

namespace Rws.LC.VerificationSampleApp.Exceptions
{
    public class VerificationException : AppException
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
