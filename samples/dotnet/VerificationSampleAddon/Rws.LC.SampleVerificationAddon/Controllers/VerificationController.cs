using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Rws.LC.SampleVerificationAddon.Infrastructure;
using Rws.LC.SampleVerificationAddon.RestService.Interfaces;
using Rws.LC.SampleVerificationAddon.RestService.Models;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.RestService.Controllers
{
    [Route("api/verification/v1")]
    [ApiController]
    public class VerificationController : ControllerBase
    {
        private ILogger _logger;
        private IVerificationService _verificationService;

        /// <summary>
        /// Initializes a new instance of the <see cref="VerificationController"/> class.
        /// </summary>
        /// <param name="logger">logger for this class instance</param>
        /// <param name="verificationService">service with main business logic</param>
        public VerificationController(ILogger<VerificationController> logger,
            IVerificationService verificationService)
        {
            _logger = logger;
            _verificationService = verificationService;
        }

        /// <summary>
        /// Starts the verification of a document.
        /// </summary>
        /// <param name="requestId"></param>
        /// <param name="verificationRequest"></param>
        /// <param name="traceId"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        [Authorize]
        [HttpPost("verify/document/{requestId}")]
        public async Task<IActionResult> StartVerification(string requestId, [FromBody] VerificationRequest verificationRequest, [FromHeader(Name = "TR_ID")] string traceId, CancellationToken token)
        {
            // TODO - ensure tenant separation in implementation
            var tenantId = HttpContext.User?.GetTenantId();
            // var tenantId = "5aa68de5e4b0c82172a4a074"; // Hardcoded for testing with LC QA environment

            _logger.LogDebug($"StartVerification called with requestId {requestId}.");
         
            var jobId = await _verificationService.StartVerificationAsync(verificationRequest, requestId, tenantId, traceId, token);

            _logger.LogDebug("StartVerification finished.");

            return Ok($"Processing jobId {jobId}");
        }


       /// <summary>
       /// Verifies a segment.
       /// </summary>
       /// <param name="verifySegmentRequest"></param>
       /// <param name="token"></param>
       /// <returns></returns>
        [Authorize]
        [HttpPost("verify/segment")]
        public async Task<IActionResult> VerifySegment([FromBody] VerifySegmentRequest verifySegmentRequest, CancellationToken token)
        {
            // TODO - ensure tenant separation in implementation
            var tenantId = HttpContext.User?.GetTenantId();

            _logger.LogDebug("VerifySegment called.");

            var result = await _verificationService.VerifySegment(verifySegmentRequest, token);

            _logger.LogDebug("VerifySegment finished.");

            return Ok(result);

        }

        /// <summary>
        /// Gets the localized messages for the specified culture. These are used to populate the UI.
        /// </summary>
        /// <param name="culture"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        [Authorize]
        [HttpGet("messages/{culture}")]
        public async Task<IActionResult> GetMessagesByCulture(string culture, CancellationToken token)
        {
            // TODO - ensure tenant separation in implementation
            var tenantId = HttpContext.User?.GetTenantId();

            _logger.LogDebug("GetMessagesByCulture called.");

            var result = await _verificationService.GetMessagesByCulture(culture, token);

            _logger.LogDebug("GetMessagesByCulture finished.");

            return Ok(result);

        }

        /// <summary>
        /// Gets the schemas related to the settings.
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
        [Authorize]
        [HttpGet("schemas")]
        public async Task<IActionResult> GetSchemas(CancellationToken token)
        {
            // TODO - ensure tenant separation in implementation
            var tenantId = HttpContext.User?.GetTenantId();

            _logger.LogDebug("GetSchemas called.");

            var result = await _verificationService.GetSchemas(token);

            _logger.LogDebug("GetSchemas finished.");

            return Ok(result);

        }



    }
}