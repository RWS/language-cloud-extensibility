using System.IO;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.UISampleApp.Helpers
{
    public static class StreamHelpers
    {
        /// <summary>
        /// Returns a JSON stream
        /// </summary>
        /// <param name="fileName">The file name.</param>
        /// <returns></returns>
        internal static async Task<byte[]> GetData(string fileName, CancellationToken cancellationToken = default)
        {
            if (!File.Exists(Path.Combine("Resources", fileName)))
            {
                return null;
            }

            return await File.ReadAllBytesAsync(Path.Combine("Resources", fileName), cancellationToken).ConfigureAwait(false);
        }
    }
}
