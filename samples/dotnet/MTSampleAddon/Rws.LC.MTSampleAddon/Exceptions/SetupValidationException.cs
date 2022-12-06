using System.Net;

namespace Rws.LC.MTSampleAddon.Exceptions
{
    public class SetupValidationException : AddonException
    {
        public SetupValidationException(string message, Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.InvalidSetup;
            StatusCode = HttpStatusCode.BadRequest;
            ExceptionDetails = details;
        }
    }
}
