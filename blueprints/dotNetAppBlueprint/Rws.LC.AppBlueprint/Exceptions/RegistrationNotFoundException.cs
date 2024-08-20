using Rws.LC.AppBlueprint.Exceptions;
using System.Net;

namespace Sdl.AppBlueprint.RestService.Exceptions
{
    public class RegistrationNotFoundException : AppException
    {
        public RegistrationNotFoundException(string tenantId, string appId) : base($"The App registration could not be found for tenant id {tenantId} and app id {appId}")
        {
            ErrorCode = ErrorCodes.RegistrationNotFound;
            StatusCode = HttpStatusCode.NotFound;
        }
    }
}
