using Microsoft.Extensions.Logging;
using Rws.LC.SampleVerificationAddon.Exceptions;
using Rws.VerificationSampleAddon.RestService.Exceptions;
using System.Collections.Generic;
using System.Net;

namespace Rws.LC.SampleVerificationAddon.RestService.Exceptions
{
    public class PublishMessageException : AddonException
    {
        public PublishMessageException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.SendCallbackException;
            StatusCode = HttpStatusCode.InternalServerError;
            ExceptionDetails = details;
        }
    }
}
