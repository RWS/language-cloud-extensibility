using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Rws.LC.VerificationSampleApp.Infrastructure;
using Rws.LC.VerificationSampleApp.Interfaces;
using Rws.LC.VerificationSampleApp.Interfaces;
using Rws.LC.VerificationSampleApp.Models;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Controllers
{
    [Route("api/verification/v1")]
    [ApiController]
    public class VerificationController : ControllerBase
    {
        /// <summary>
        /// The configuration
        /// </summary>
        private IConfiguration _configuration;

        /// <summary>
        /// The logger.
        /// </summary>
        private ILogger _logger;

        /// <summary>
        /// The descriptor service.
        /// </summary>
        private IDescriptorService _descriptorService;

        /// <summary>
        /// The account service.
        /// </summary>
        private IAccountService _accountService;

        /// <summary>
        /// The Verification Service
        /// </summary>
        private IVerificationService _verificationService;

        /// <summary>
        /// The health reporter.
        /// </summary>
        private IHealthReporter _healthReporter;

        /// <summary>
        /// Initializes a new instance of the <see cref="AccountService"/> class.
        /// </summary>
        /// <param name="configuration">The configuration.</param>
        /// <param name="logger">The logger.</param>
        /// <param name="descriptorService">The descriptor service.</param>
        /// <param name="accountService">The account service.</param>
        /// <param name="healthReporter">The health reporter.</param>
        public VerificationController(IConfiguration configuration,
            ILogger<VerificationController> logger,
            IDescriptorService descriptorService,
            IAccountService accountService,
            IVerificationService verificationService,
            IHealthReporter healthReporter)
        {
            _configuration = configuration;
            _logger = logger;
            _descriptorService = descriptorService;
            _accountService = accountService;
            _verificationService = verificationService;
            _healthReporter = healthReporter;
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