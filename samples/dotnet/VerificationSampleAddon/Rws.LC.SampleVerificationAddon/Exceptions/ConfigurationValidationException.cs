using Rws.VerificationSampleAddon.RestService.Exceptions;
using System.Net;

namespace Rws.LC.SampleVerificationAddon.Exceptions
{
    public class ConfigurationValidationException : AddonException
    {
        public ConfigurationValidationException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.InvalidConfiguration;
            StatusCode = HttpStatusCode.BadRequest;
            ExceptionDetails = details;
        }
    }
}
