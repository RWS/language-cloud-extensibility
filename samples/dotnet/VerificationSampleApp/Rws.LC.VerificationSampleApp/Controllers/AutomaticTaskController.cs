using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Rws.LC.VerificationSampleApp.Infrastructure;
using Rws.LC.VerificationSampleApp.Models;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Controllers
{
    [Route("v1")]
    [ApiController]
    // TODO: this controller is a placeholder for AutomaticTask extensions, either implement it or remove it
    public class AutomaticTaskController : ControllerBase
    {
        /// <summary>
        /// The logger.
        /// </summary>
        private readonly ILogger _logger;

        /// <summary>
        /// Initializes a new instance of the <see cref="AutomaticTaskController"/> class.
        /// </summary>
        /// <param name="logger">The logger.</param>
        public AutomaticTaskController(ILogger<AutomaticTaskController> logger)
        {
            _logger = logger;
        }

        /// <summary>
        /// Receive task to be processed.
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpPost("submit")]
        public async Task<IActionResult> ProcessTask(AutomaticTaskRequestModel automaticTaskRequest)
        {
            // Endpoint used to receive and process the task from LC
            // This endpoint should only schedule the task and return 202(Accepted)
            // The scheduled task would be picked up by a background process
            // that will send the result to the received callbackUrl
            _logger.LogInformation("Received automatic task request from LC");

            var tenantId = HttpContext.User?.GetTenantId();

            // TODO: Replace the following line with your actual processing task(implementation needed)
            await Task.CompletedTask.ConfigureAwait(false);

            return Accepted();
        }
    }
}
