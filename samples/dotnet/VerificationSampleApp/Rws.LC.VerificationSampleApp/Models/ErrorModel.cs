
using Rws.LC.VerificationSampleApp.Exceptions;

namespace Rws.LC.VerificationSampleApp.Models
{
    public class ErrorModel
    {
        public string ErrorCode { get; set; }
        public string ErrorMessage { get; set; }
        public Details[] Details { get; set; }
    }
}
