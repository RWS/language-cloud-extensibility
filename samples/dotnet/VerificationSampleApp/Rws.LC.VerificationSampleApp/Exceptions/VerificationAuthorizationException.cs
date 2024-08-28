using System.Net;

namespace Rws.LC.VerificationSampleApp.Exceptions
{
    public class VerificationAuthorizationException : AppException
    {
        public VerificationAuthorizationException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.VerificationException;
            StatusCode = HttpStatusCode.Unauthorized;
            ExceptionDetails = details;
        }
    }
}
