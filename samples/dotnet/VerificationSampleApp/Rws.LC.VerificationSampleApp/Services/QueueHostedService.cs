using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Rws.LC.VerificationSampleApp.Interfaces;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Services
{
    public class QueuedHostedService : BackgroundService
    {
        private readonly ILogger<QueuedHostedService> _logger;

        private readonly Task[] _executors;
        private readonly int _executorsCount = 2; //--default value: 2
        private CancellationTokenSource _tokenSource;
        public IBackgroundTaskQueue TaskQueue { get; }

        public QueuedHostedService(IBackgroundTaskQueue taskQueue, IConfiguration configuration,
            ILogger<QueuedHostedService> logger)

        {
            TaskQueue = taskQueue;
            _logger = logger;
            if (int.TryParse(configuration["MaxNumOfParallelOperations"], out var ct))

            {
                _executorsCount = ct;
                _logger.LogInformation($"Set maximum number of background workers to {ct}");
            }
            _executors = new Task[_executorsCount];
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _logger.LogInformation(
                $"Queued Hosted Service is running.");

            await BackgroundProcessing(stoppingToken);
        }

        private async Task BackgroundProcessing(CancellationToken cancellationToken)
        {
            _tokenSource = CancellationTokenSource.CreateLinkedTokenSource(cancellationToken);

            for (var i = 0; i < _executorsCount; i++)
            {
                var executorTask = new Task(
                    async () =>
                    {
                        while (!cancellationToken.IsCancellationRequested)
                        {
                            _logger.LogDebug("Waiting background task...");



                            try
                            {
                                var workItem = await TaskQueue.DequeueAsync(_tokenSource.Token);
                                if (workItem != null)
                                {
                                    try
                                    {
                                        _logger.LogDebug("Got background task, executing...");
                                        await workItem(_tokenSource.Token);
                                    }
                                    catch (Exception ex)
                                    {
                                        _logger.LogError(ex,
                                            "Error occurred executing {WorkItem}.", nameof(workItem)
                                        );
                                    }
                                }
                            }
                            catch (OperationCanceledException) // On graceful shutdown, we should receive this exception here
                            {
                                break;
                            }
                            catch (Exception ex)
                            {
                                _logger.LogError(ex, "Error occurred during DequeueAsync.", ex.Message);
                            }
                        }
                    }, _tokenSource.Token);

                _executors[i] = executorTask;
                executorTask.Start();
            }

            await Task.WhenAll(_executors);
        }

        public override async Task StopAsync(CancellationToken stoppingToken)
        {
            _logger.LogInformation("Queue Hosted Service is stopping.");
            _tokenSource.Cancel(); // send the cancellation signal

            if (_executors != null)
            {
                // wait for _executors completion
                await Task.WhenAll(_executors);
            }
            _logger.LogInformation("Queue Hosted Service has stopped.");

            await base.StopAsync(stoppingToken);
        }
    }
}

