using System;
using System.Net;

namespace Rws.LC.VerificationSampleApp.Exceptions
{
    public class AccountValidationException : AppException
    {
        public AccountValidationException(string message, Exception inner, params Details[] details) : base(message, inner)
        {
            ErrorCode = ErrorCodes.InvalidAccount;
            StatusCode = HttpStatusCode.Unauthorized;
            ExceptionDetails = details;
        }

        public AccountValidationException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.InvalidAccount;
            StatusCode = HttpStatusCode.Unauthorized;
            ExceptionDetails = details;
        }
    }
}
