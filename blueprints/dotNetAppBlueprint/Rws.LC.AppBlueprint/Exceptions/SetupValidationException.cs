using System.Net;

namespace Rws.LC.AppBlueprint.Exceptions
{
    public class SetupValidationException : AppException
    {
        public SetupValidationException(string message, Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.InvalidSetup;
            StatusCode = HttpStatusCode.BadRequest;
            ExceptionDetails = details;
        }
    }
}
