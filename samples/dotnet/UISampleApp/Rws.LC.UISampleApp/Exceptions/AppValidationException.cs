using System;
using System.Net;

namespace Rws.LC.UISampleApp.Exceptions
{
    public class AppValidationException : AppException
    {
        public AppValidationException(string message, Exception inner, params Details[] details) : base(message, inner)
        {
            ErrorCode = ErrorCodes.InvalidValues;
            StatusCode = HttpStatusCode.BadRequest;
            ExceptionDetails = details;
        }

        public AppValidationException(string message, params Details[] details) : base(message)
        {
            ErrorCode = ErrorCodes.InvalidValues;
            StatusCode = HttpStatusCode.BadRequest;
            ExceptionDetails = details;
        }
    }
}
