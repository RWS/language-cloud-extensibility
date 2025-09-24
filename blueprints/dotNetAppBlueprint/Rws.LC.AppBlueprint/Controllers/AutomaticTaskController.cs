using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Rws.LC.AppBlueprint.Infrastructure;
using Rws.LC.AppBlueprint.Models;
using Rws.LanguageCloud.Sdk.Authentication;
using Sdl.ApiClientSdk.Core;
using Rws.LC.AppBlueprint.DAL;
using Rws.LC.AppBlueprint.Interfaces;
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

    private readonly IRepository _repository;

    private readonly LanguageCloudClientFactory _languageCloudClientFactory;

        /// <summary>
        /// Initializes a new instance of the <see cref="AutomaticTaskController"/> class.
        /// </summary>
        /// <param name="logger">The logger.</param>
        public AutomaticTaskController(ILogger<AutomaticTaskController> logger, IRepository repository, LanguageCloudClientFactory languageCloudClientFactory)
        {
            _logger = logger;
            _repository = repository;
            _languageCloudClientFactory = languageCloudClientFactory;
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

            // Just as an example we'll try to make a Public API call
            var accountInfo = await _repository.GetAccountInfoByTenantId(tenantId).ConfigureAwait(false);
            string region = accountInfo?.Region;

            // getting project details as an example of using the LC SDK, with implicit tenant from the current Http Context
            var projectDetails = await _languageCloudClientFactory.Region(region).ProjectClient.GetProjectAsync(automaticTaskRequest.ProjectId);

            // Example of using explicit tenant context, if you need to process a task for a different tenant than the current one, for example in background jobs with no Http Context.
            using (Context.BeginScope(tenantId))
            {
                // getting project details as an example of using the LC SDK
                var projectDetailsForExplicitTenant = await _languageCloudClientFactory.Region(region).ProjectClient.GetProjectAsync(automaticTaskRequest.ProjectId);
            }
            
            // TODO: Replace the following line with your actual processing task(implementation needed)
            await Task.CompletedTask.ConfigureAwait(false);

            return Accepted();
        }
    }
}
