using System.Net;

namespace Rws.LC.AddonBlueprint.Exceptions
{
    public class ConfigurationValidationException : AddonException
    {
        public ConfigurationValidationException(string message, Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.InvalidConfiguration;
            StatusCode = HttpStatusCode.BadRequest;
            ExceptionDetails = details;
        }
    }
}
