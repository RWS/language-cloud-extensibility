using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.StaticFiles;
using Microsoft.Extensions.Logging;
using Rws.LC.UISampleApp.Helpers;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.UISampleApp.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class FilesController : ControllerBase
    {
        /// <summary>
        /// The logger.
        /// </summary>
        private ILogger<FilesController> logger;

        public FilesController(ILogger<FilesController> logger)
        {
            this.logger = logger;
        }

        /// <summary>
        /// Gets the embedded script.
        /// </summary>
        /// <param name="relativePath"></param>
        /// <param name="cancellationToken"></param>
        /// <returns>The descriptor</returns>
        [HttpGet("{*relativePath}")]
        public async Task<IActionResult> GetScript(string relativePath, CancellationToken cancellationToken = default)
        {
            // support path instead of filename directly
            //if (relativePath?.IndexOfAny(Path.GetInvalidFileNameChars()) > 0)
            //{
            //    return NotFound($"Resource {relativePath} not found.");
            //}

            string fileName = string.IsNullOrWhiteSpace(relativePath) ? "Script.js" : relativePath;

            byte[] fileStream = await StreamHelpers.GetData(fileName, cancellationToken).ConfigureAwait(true);

            if (fileStream == null)
            {
                return NotFound($"Resource {relativePath} not found.");
            }

            string mimeType = string.Empty;
            new FileExtensionContentTypeProvider().TryGetContentType(fileName, out mimeType);

            return File(fileStream, mimeType, fileName);
        }
    }
}
