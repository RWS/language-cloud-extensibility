using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Rws.LC.AppBlueprint.Infrastructure;
using Rws.LC.AppBlueprint.Models;
using System.IO;
using System.Threading.Tasks;

namespace Rws.LC.AppBlueprint.Controllers
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
        public async Task<IActionResult> ProcessTask()
        {
            // Endpoint used to receive and process the task from LC
            // This endpoint should only schedule the task and return 202(Accepted)
            // The scheduled task would be picked up by a background process
            // that will send the result to the received callbackUrl
            _logger.LogInformation("Received automatic task request from LC");

            // we deserialize this way to get the dynamic value from WorkflowConfiguration
            string payload;
            using (StreamReader sr = new StreamReader(Request.Body))
            {
                payload = await sr.ReadToEndAsync();
            }

            var tenantId = HttpContext.User?.GetTenantId();
            var automaticTaskRequest = Newtonsoft.Json.JsonConvert.DeserializeObject<AutomaticTaskRequestModel>(payload);

            // TODO: Replace the following line with your actual processing task(implementation needed)
            await Task.CompletedTask.ConfigureAwait(false);

            return Accepted();
        }
    }
}
