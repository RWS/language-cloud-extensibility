using System;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Interfaces
{
        public interface IBackgroundTaskQueue
    {
        ValueTask QueueBackgroundWorkItemAsync(Func<CancellationToken, ValueTask> workItem, CancellationToken cancellationToken);

        ValueTask<Func<CancellationToken, ValueTask>> DequeueAsync(
            CancellationToken cancellationToken);
    }

}
