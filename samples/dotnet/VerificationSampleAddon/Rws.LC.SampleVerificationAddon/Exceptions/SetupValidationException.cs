using Rws.VerificationSampleAddon.RestService.Exceptions;
using System.Net;

namespace Rws.LC.SampleVerificationAddon.Exceptions
{
    public class SetupValidationException : AddonException
    {
        public SetupValidationException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.InvalidSetup;
            StatusCode = HttpStatusCode.BadRequest;
            ExceptionDetails = details;
        }
    }
}
