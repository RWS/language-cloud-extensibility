namespace Rws.VerificationSampleAddon.RestService.Exceptions
{
    /// <summary>
    /// The error codes as string constants.
    /// </summary>
    public static class ErrorCodes
    {
        public const string InvalidValues = "INVALID_VALUES";
        public const string InvalidType = "INVALID_TYPE";
        public const string InternalServerError = "INTERNAL_SERVER_ERROR";
        public const string InvalidAccount = "INVALID_ACCOUNT_ID";
        public const string AccountNotActivated = "ACCOUNT_NOT_FOUND";
        public const string AccountAlreadyActivated = "ACCOUNT_ALREADY_ACTIVATED";
        public const string InvalidInput = "INVALID_INPUT";
        public const string InternalError = "INTERNAL_ERROR";
        public const string InvalidConfiguration = "invalidConfiguration";
        public const string InvalidSetup = "invalidSetup";
        public const string InvalidValue = "invalidValue";
        public const string InvalidKey = "invalidKey";
        public const string NullValue = "nullValue";
        public const string AlreadyRegistered = "alreadyRegistered";
        public const string VerificationException = "VerificationError";
	    public const string FileUploadException = "FileUploadError";
        public const string FileDownloadException = "FileDownloadError";
        public const string SendCallbackException = "SendCallbackError";
    }
}
