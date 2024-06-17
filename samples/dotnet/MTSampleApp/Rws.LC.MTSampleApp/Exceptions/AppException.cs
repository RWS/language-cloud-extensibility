using System;
using System.Net;

namespace Rws.LC.MTSampleApp.Exceptions
{
    public class AppException : Exception
    {
        public string ErrorCode { get; set; }
        public HttpStatusCode StatusCode { get; set; }
        public Details[] ExceptionDetails { get; set; }

        public AppException()
        {
            ErrorCode = ErrorCodes.InternalServerError;
            StatusCode = HttpStatusCode.InternalServerError;
        }

        public AppException(string message)
            : base(message)
        {
            ErrorCode = ErrorCodes.InternalServerError;
            StatusCode = HttpStatusCode.InternalServerError;
        }

        public AppException(string message, Exception inner)
            : base(message, inner)
        {
            ErrorCode = ErrorCodes.InternalServerError;
            StatusCode = HttpStatusCode.InternalServerError;
        }

        public AppException(HttpStatusCode statusCode, string errorCode, string message)
            : base(message)
        {
            ErrorCode = errorCode;
            StatusCode = statusCode;
        }

        public AppException(HttpStatusCode statusCode, string errorCode, string message, Details[] details)
            : base(message)
        {
            ErrorCode = errorCode;
            StatusCode = statusCode;
            ExceptionDetails = details;
        }

        public AppException(HttpStatusCode statusCode, string errorCode, string message, Exception inner)
            : base(message, inner)
        {
            ErrorCode = errorCode;
            StatusCode = statusCode;
        }

        public AppException(HttpStatusCode statusCode, string errorCode, string message, Details[] details, Exception inner)
            : base(message, inner)
        {
            ErrorCode = errorCode;
            StatusCode = statusCode;
            ExceptionDetails = details;
        }
    }
}
