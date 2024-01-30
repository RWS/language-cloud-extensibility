using Rws.VerificationSampleAddon.RestService.Exceptions;
using System;
using System.Net;

namespace Rws.LC.SampleVerificationAddon.Exceptions
{
    public class AccountValidationException : AddonException
    {
        public AccountValidationException(string message, Exception inner, params Details[] details) : base(message, inner)
        {
            ErrorCode = ErrorCodes.InvalidAccount;
            StatusCode = HttpStatusCode.Unauthorized;
            ExceptionDetails = details;
        }
    }
}
