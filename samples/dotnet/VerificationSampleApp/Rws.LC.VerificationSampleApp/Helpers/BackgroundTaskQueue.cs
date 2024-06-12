using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Rws.LC.VerificationSampleApp.Interfaces;
using System;
using System.Threading;
using System.Threading.Channels;
using System.Threading.Tasks;


namespace Rws.LC.VerificationSampleApp.Helpers
{
    public class BackgroundTaskQueue : IBackgroundTaskQueue
    {
        private readonly Channel<Func<CancellationToken, ValueTask>> _queue;
        private readonly ILogger _logger;

        public BackgroundTaskQueue(IConfiguration configuration, ILoggerFactory loggerFactory)
        {
            _logger = loggerFactory.CreateLogger<BackgroundTaskQueue>();
            // Capacity should be set based on the expected application load and
            // number of concurrent threads accessing the queue.            
            // BoundedChannelFullMode.Wait will cause calls to WriteAsync() to return a task,
            // which completes only when space became available. This leads to backpressure,
            // in case too many publishers/calls start accumulating.
            if (int.TryParse(configuration["MaxNumOfQueuedRequests"], out var maxQueuedRequests))
            {
                _logger.LogInformation($"Set maximum number of concurrent requests to {maxQueuedRequests}");
            }
            if (maxQueuedRequests <= 0)
            {
                maxQueuedRequests = 5;
            }

            var options = new BoundedChannelOptions(maxQueuedRequests)
            {
                FullMode = BoundedChannelFullMode.Wait
            };

            _queue = Channel.CreateBounded<Func<CancellationToken, ValueTask>>(options);
        }

        public async ValueTask QueueBackgroundWorkItemAsync(
            Func<CancellationToken, ValueTask> workItem, CancellationToken cancellationToken)
        {
            if (workItem == null)
            {
                throw new ArgumentNullException(nameof(workItem));
            }

            await _queue.Writer.WriteAsync(workItem, cancellationToken);
        }

        public async ValueTask<Func<CancellationToken, ValueTask>> DequeueAsync(

            CancellationToken cancellationToken)
        {
            var workItem = await _queue.Reader.ReadAsync(cancellationToken);


            return workItem;
        }
    }
}


