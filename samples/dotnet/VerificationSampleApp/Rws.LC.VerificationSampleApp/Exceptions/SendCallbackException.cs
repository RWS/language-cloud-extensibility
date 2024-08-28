using Rws.LC.VerificationSampleApp.Exceptions;
using System.Net;

namespace Rws.LC.VerificationSampleApp.Exceptions
{
    public class SendCallbackException : AppException
    {
        public SendCallbackException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.SendCallbackException;
            StatusCode = HttpStatusCode.InternalServerError;
            ExceptionDetails = details;
        }
    }
}
