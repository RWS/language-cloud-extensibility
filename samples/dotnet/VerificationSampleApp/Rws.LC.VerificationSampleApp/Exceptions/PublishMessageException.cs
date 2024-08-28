using System.Net;

namespace Rws.LC.VerificationSampleApp.Exceptions
{
    public class PublishMessageException : AppException
    {
        public PublishMessageException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.SendCallbackException;
            StatusCode = HttpStatusCode.InternalServerError;
            ExceptionDetails = details;
        }
    }
}
