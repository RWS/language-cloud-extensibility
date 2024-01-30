using Microsoft.Extensions.Logging;
using Rws.LC.SampleVerificationAddon.Exceptions;
using Rws.VerificationSampleAddon.RestService.Exceptions;
using System.Net;

namespace Rws.LC.SampleVerificationAddon.RestService.Exceptions
{
    public class SendCallbackException : AddonException
    {
        public SendCallbackException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.SendCallbackException;
            StatusCode = HttpStatusCode.InternalServerError;
            ExceptionDetails = details;
        }
    }
}
