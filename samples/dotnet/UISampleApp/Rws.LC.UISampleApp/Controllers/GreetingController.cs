using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System.Text.Json.Nodes;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.UISampleApp.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class GreetingController : ControllerBase
    {
        /// <summary>
        /// The logger.
        /// </summary>
        private ILogger<GreetingController> logger;

        public GreetingController(ILogger<GreetingController> logger)
        {
            this.logger = logger;
        }

        /// <summary>
        /// This endpoint always returns "Hello world!".
        /// </summary>
        /// <returns>The hardcoded greeting message.</returns>
        [Authorize]
        [HttpGet("")]
        public ActionResult GetGreeting()
        {
            logger.LogInformation("Retrieving greeting.");
            return Ok(new JsonObject() { { "greeting", "Hello, world!" } });
        }
    }
}
