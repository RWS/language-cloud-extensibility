using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Rws.LC.MTSampleApp.Infrastructure;
using Rws.LC.MTSampleApp.Interfaces;
using Rws.LC.MTSampleApp.Models;
using System.Threading.Tasks;

namespace Rws.LC.MTSampleApp.Controllers
{
    [Route("v1")]
    [ApiController]
    // TODO: this controller is a placeholder for MT extensions, either implement it or remove it
    public class TranslationController : ControllerBase
    {
        /// <summary>
        /// The logger.
        /// </summary>
        private readonly ILogger _logger;

        /// <summary>
        /// The translation service.
        /// </summary>
        private readonly ITranslationService _translationService;

        /// <summary>
        /// Initializes a new instance of the <see cref="TranslationController"/> class.
        /// </summary>
        /// <param name="logger">The logger.</param>
        public TranslationController(ILogger<TranslationController> logger, ITranslationService translationService)
        {
            _logger = logger;
            _translationService = translationService;
        }

        /// <summary>
        /// Receive content to be tranlated.
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpPost("translate")]
        public async Task<IActionResult> Translate(TranslationRequestModel translationRequest)
        {
            // Endpoint used to receive and translate the contents from LC
            _logger.LogInformation("Received translation request from LC");

            var tenantId = HttpContext.User?.GetTenantId();

            var result = await _translationService.Translate(tenantId, translationRequest).ConfigureAwait(false);

            return Ok(result);
        }

        /// <summary>
        /// Returns the available transation engines
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpGet("translation-engines")]
        public async Task<IActionResult> GetTranslationEngines([FromQuery] TranslationEnginesRequestModel translationEnginesRequest)
        {
            // Endpoint used to retrieve the available translation engines(language pairs)
            _logger.LogInformation("Getting translation engines");

            var tenantId = HttpContext.User?.GetTenantId();

            var result = await _translationService.GetTranslationEngines(tenantId, translationEnginesRequest).ConfigureAwait(false);

            return Ok(result);
        }

        /// <summary>
        /// Returns the translation engine by id
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpGet("translation-engines/{id}")]
        public async Task<IActionResult> GetTranslationEngine(string id)
        {
            // Endpoint used to retrieve the translation engine(language pair) by id
            _logger.LogInformation($"Getting translation engine by id {id}");

            // TODO: Replace the following line with your actual "GetEngine" task(implementation needed)
            var result = new TranslationEngineModel();

            return Ok(result);
        }
    }
}
