using Rws.LC.SampleVerificationAddon.Exceptions;

namespace Rws.VerificationSampleAddon.RestService.Models
{
    public class ErrorModel
    {
        public string ErrorCode { get; set; }
        public string ErrorMessage { get; set; }
        public Details[] Details { get; set; }
    }
}
