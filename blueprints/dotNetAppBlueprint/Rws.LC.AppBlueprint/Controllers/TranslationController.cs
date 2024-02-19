using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Rws.LC.AppBlueprint.Helpers;
using Rws.LC.AppBlueprint.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Rws.LC.AppBlueprint.Controllers
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
        /// Initializes a new instance of the <see cref="TranslationController"/> class.
        /// </summary>
        /// <param name="logger">The logger.</param>
        public TranslationController(ILogger<TranslationController> logger)
        {
            _logger = logger;
        }

        /// <summary>
        /// Receive content to be translated.
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpPost("translate")]
        public async Task<IActionResult> Translate(TranslationRequestModel translationRequest)
        {
            // Endpoint used to receive and translate the contents from LC
            _logger.LogInformation("Received translation request from LC");

            // TODO: Replace the following line with your actual "Translate" task(implementation needed)
            var result = new TranslationsModel();

            return Ok(result);
        }

        /// <summary>
        /// Returns the available translation engines
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpGet("translation-engines")]
        public async Task<IActionResult> GetTranslationEngines([FromQuery] TranslationEnginesRequestModel translationEnginesRequest)
        {
            // Endpoint used to retrieve the available translation engines(language pairs)
            _logger.LogInformation("Getting translation engines");

            // Example how to retrieve headers information
            string extensionPointVersion = Request.HttpContext.Request.Headers.SingleOrDefault(h => h.Key.Equals(Constants.ExtensionPointVersion, StringComparison.OrdinalIgnoreCase)).Value;
            string extensionId = Request.HttpContext.Request.Headers.SingleOrDefault(h => h.Key.Equals(Constants.ExtensionId, StringComparison.OrdinalIgnoreCase)).Value;
            string appVersion = Request.HttpContext.Request.Headers.SingleOrDefault(h => h.Key.Equals(Constants.AddonVersion, StringComparison.OrdinalIgnoreCase)).Value;

            // TODO: Replace the following line with your actual "GetEngines" task(implementation needed)
            var result = new TranslationEnginesResult(new List<TranslationEngineModel>() { new TranslationEngineModel() });

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
