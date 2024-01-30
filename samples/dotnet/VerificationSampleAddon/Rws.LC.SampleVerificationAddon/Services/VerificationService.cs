using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Rws.LC.SampleVerificationAddon.RestService.Exceptions;
using Rws.LC.SampleVerificationAddon.RestService.Interfaces;
using Rws.LC.SampleVerificationAddon.RestService.Models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Configuration;
using System.Text;
using Rws.LC.SampleVerificationAddon.Verifiers.Interfaces;
using Rws.LC.SampleVerificationAddon.Verifiers.Events;
using Rws.LC.SampleVerificationAddon.Verifiers.Models;
using Sdl.Core.Bcm.BcmModel;
using Rws.LC.SampleVerificationAddon.Verifiers.Other;
using System.Globalization;
using Rws.LC.SampleVerificationAddon.Verifiers.Resources;
using Rws.LC.SampleVerificationAddon.Verifiers.Settings;
using System.Collections.Concurrent;
using Rws.LC.SampleVerificationAddon.Exceptions;
using Rws.VerificationSampleAddon.RestService.Models;

namespace Rws.LC.SampleVerificationAddon.RestService.Services
{
    /// <summary>
    /// Main Verification Extension Service
    /// </summary>
    public class VerificationService : IVerificationService
    {
        private readonly ILogger _logger;

        private IBackgroundTaskQueue _queue { get; }
        private readonly IServiceScopeFactory _serviceScopeFactory;
        private IFileManagementServiceClient _fileManagementServiceClient;
        private IExternalJobServiceClient _externalJobServiceClient;
        private IConfiguration _configuration;
        private IBcmServiceClient _bcmServiceClient;
        private IVerificationServiceClient _verificationServiceClient;
        private IVerifierFactory _verifierFactory;
        private ConcurrentDictionary<string, List<PublishMessageEventArgs>> _messageBatchSessionEntries;


        /// <summary>
        /// Initializes a new instance of the <see cref="VerificationService"/> class.
        /// </summary>
        /// <param name="configuration"></param>
        /// <param name="fileManagementServiceClient"></param>
        /// <param name="bcmServiceClient"></param>
        /// <param name="verificationServiceClient"></param>
        /// <param name="externalJobServiceClient"></param>
        /// <param name="queue"></param>
        /// <param name="serviceScopeFactory"></param>
        /// <param name="logger"></param>
        /// <param name="verifierFactory"></param>
        public VerificationService(IConfiguration configuration,
             IFileManagementServiceClient fileManagementServiceClient,
             IBcmServiceClient bcmServiceClient,
             IVerificationServiceClient verificationServiceClient,
             IExternalJobServiceClient externalJobServiceClient,
             IBackgroundTaskQueue queue,
             IServiceScopeFactory serviceScopeFactory,
             ILogger<VerificationService> logger,
             IVerifierFactory verifierFactory)
        { // Reading from the descriptor.json file, the descriptor for this Add-On. 
            // Customize it to represent your Add-On behavior.
            _configuration = configuration;
            _logger = logger;
            _fileManagementServiceClient = fileManagementServiceClient;
            _bcmServiceClient = bcmServiceClient;
            _verificationServiceClient = verificationServiceClient;
            _externalJobServiceClient = externalJobServiceClient;
            _queue = queue;
            _serviceScopeFactory = serviceScopeFactory;
            _verifierFactory = verifierFactory;
            _messageBatchSessionEntries = new ConcurrentDictionary<string, List<PublishMessageEventArgs>>();
        }

        /// <summary>
        /// Starts an asynchronous background job to validate the input document. A callback will be made on the callback URL when this asynchronous job has completed
        /// </summary>
        /// <param name="request"></param>
        /// <returns></returns>
        public async Task<string> StartVerificationAsync(VerificationRequest request, string requestId, string tenantId, string traceId, CancellationToken cancellationToken)
        {
            ValidateRequest(request);
            LogRequestDetails(request);

            var jobId = Guid.NewGuid().ToString();
            _logger.LogInformation($"Queuing job with ID {jobId} for StartVerification with VerificationSessionId {request.SessionId}");
            await _queue.QueueBackgroundWorkItemAsync(token => BuildVerificationWorkItem(request, tenantId, jobId, requestId, traceId, cancellationToken), cancellationToken);

            return jobId;
        }


        /// <summary>
        /// Generates a set of verification results for the input list of BCM fragments
        /// </summary>
        /// <param name="request"></param>
        /// <returns>Response with message details</returns>
        public async Task<VerifySegmentResponse> VerifySegment(VerifySegmentRequest request, CancellationToken cancellationToken)
        {
            var response = new VerifySegmentResponse();

            var fragment = JsonConvert.DeserializeObject<Fragment>(request.Fragment.ToString());
            var segmentPairs = fragment.GetSegmentPairs();

            var segmentVerifier = _verifierFactory.GetSegmentVerifier();

            var settings = string.IsNullOrEmpty(request?.VerifierSettings?.ToString()) ? null : JsonConvert.DeserializeObject<SourceTargetLengthVerifierSettings>(request?.VerifierSettings?.ToString());

            _logger.LogInformation($"Settings received: {request?.VerifierSettings?.ToString()}");

            List<VerificationMessage> messages = new List<VerificationMessage>();

            segmentVerifier.PublishMessage += (sender, args) => { var publishMessageEventArgs = args as PublishMessageEventArgs; messages.Add(publishMessageEventArgs.Message); };

            await segmentVerifier.Verify(segmentPairs.FirstOrDefault(), request.SegmentLocation, settings, 1);

            segmentVerifier.PublishMessage -= (sender, args) => {};

            response.VerificationMessages = messages;
            var firstMessage = messages.FirstOrDefault();

            return response;
        }

        /// <summary>
        /// Returns localized messages for the specified culture - this is used by the UI to display the messages in the correct language
        /// </summary>
        /// <param name="culture"></param>
        /// <param name="cancellationToken"></param>
        /// <returns></returns>
        public Task<MessageTypeResponse> GetMessagesByCulture(string culture, CancellationToken cancellationToken)
        {
            var response = new MessageTypeResponse();

            MessageResource.Culture = CultureInfo.GetCultureInfo(culture);
            
            response.Culture = culture;
            var messageTypeLocalizationData = new MessageTypeLocalizationData 
            {
                DetailedErrorMessage = MessageResource.MessageLengthExceeded_DetailedDescription,
                ErrorMessage = MessageResource.MessageLengthExceeded_EM,
                FriendlyName = MessageResource.MessageLengthExceeded_FriendlyName,
                MessageType = "lc-verification-sample.LengthCheck",
                Suggestion = MessageResource.MessageLengthExceeded_Suggestion
            };
            response.MessageTypes = new List<MessageTypeLocalizationData> { messageTypeLocalizationData};   
            return Task.FromResult(response);
        }

        /// <summary>
        /// Returns the schema for the settings object that is used to configure the verification process
        /// </summary>
        /// <param name="cancellationToken"></param>
        /// <returns></returns>
        public Task<SettingsSchemaResponse> GetSchemas(CancellationToken cancellationToken)
        {
            var response = new SettingsSchemaResponse();

            var schema = Encoding.UTF8.GetString(SettingsSchemaResource.SettingsSchema);
            var deserializedObject = JsonConvert.DeserializeObject(schema);

            response.Group = "sdl.project.verification.addon";
            response.SystemId = "lc-verification-sample";

            response.Schema = JsonConvert.SerializeObject(deserializedObject);

            return Task.FromResult(response);
        }

        /// <summary>
        /// Builds a work item for the background job queue
        /// </summary>
        /// <param name="request"></param>
        /// <param name="tenantId"></param>
        /// <param name="jobId"></param>
        /// <param name="requestId"></param>
        /// <param name="traceId"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        private async ValueTask BuildVerificationWorkItem(VerificationRequest request, string tenantId, string jobId, string requestId, string traceId, CancellationToken token)
        {
            var errors = new List<ErrorModel>();

            var timestamp = DateTime.Now.ToUniversalTime();

            string name = "generateVerification.Start";

            var messageCount = 0;
            var result = "Success";

            try
            {
                var scope = new[] {

                        new KeyValuePair<string, object>("TR_ID", traceId) 
                };


                using (_logger.BeginScope(scope))
                {

                    _logger.LogInformation($"Starting Background Job {jobId} - {timestamp}");

                    var settings = string.IsNullOrEmpty(request?.VerifierSettings?.ToString()) ? null: JsonConvert.DeserializeObject<SourceTargetLengthVerifierSettings>(request?.VerifierSettings?.ToString());

                    _logger.LogInformation($"Settings received: {request?.VerifierSettings?.ToString()}");
                    // Implement main StartVerification Functionality here. Needs to call back to External Job Service when complete

                    // Download the BCM document via Public API
                    _logger.LogInformation($"Downloading BCM document {request.InputResourceDetails.BilingualDocumentUrl} - {timestamp}");
                    var bcmSerializer = await _bcmServiceClient.DownloadBcmDocument(request.InputResourceDetails.BilingualDocumentUrl, new CancellationToken());

                    _logger.LogInformation($"Retrieving verifier - LengthCheckVerifier");
                    var verifier = _verifierFactory.GetBilingualDocumentVerifier(request.PublishMessageUrl, request.SessionId, requestId, traceId);

                    verifier.PublishMessage += Verifier_PublishMessage;

                    _logger.LogInformation($"Verifying document");
                    
                    try
                    {
                        messageCount = await verifier.Verify(bcmSerializer, settings);
                        // One less than current value
                        messageCount--;
                    }
                    catch (Exception ex) 
                    {
                        result = "Fail";
                        _logger.LogError($"Error during verification - SessionId: {request.SessionId} - {ex.ToString()}");
                        // Add error to errors here
                        throw;
                    }
                    
                    _logger.LogInformation($"Retrieving native annotated file {request.InputResourceDetails.NativeAnnotatedFileUrl} - {timestamp}");
                    // Get nativeAnnotatedFile from LC via PublicApi
                    //var nativeAnnotatedFilePath = await _fileManagementServiceClient.DownloadFileAsync(request.InputResourceDetails.NativeAnnotatedFileUrl, downloadFileName, token);

                    //_logger.LogInformation($"Retrieved native annotated file and stored at {nativeAnnotatedFilePath} - {timestamp}");

                }
            }
            catch (Exception ex)
            {
                LogError(errors, ex);
            }

            // Create callback response
            var response = new VerificationResponse() { VerificationSessionId = request.SessionId, VerificationResult = result, Errors = errors, MessageCount = messageCount };

            // Make the callback
            try
            {
                _logger.LogInformation($"Executing callback to {request.CallbackUrl}, Response.VerificationSessionId {response.VerificationSessionId}, Response.ResultUrl: {response.VerificationResult} - {timestamp}");
                await _externalJobServiceClient.SendCallback(request.CallbackUrl, response, tenantId, token);
                _logger.LogInformation($"Callback executed to {request.CallbackUrl} - {timestamp}");
            }
            catch (Exception ex)
            {
                LogError(errors, ex);
            }

            timestamp = DateTime.Now.ToUniversalTime();
            _logger.LogInformation($"Finishing Background Job {jobId} - {timestamp}");

        }

        /// <summary>
        /// Publishes a batch of messages to the specified endpoint
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Verifier_PublishMessage(object sender, EventArgs e)
        {
            var publishMessageEventArgs = e as PublishMessageEventArgs;
            if ( publishMessageEventArgs != null )
            {
                _logger.LogInformation($"Publishing message to {publishMessageEventArgs.PublishMessageEndpoint}, for verifier {publishMessageEventArgs.Message.Verifier}");
                try
                {
                    BatchMessage(publishMessageEventArgs);
                }
                catch (Exception ex) 
                {
                    _logger.LogError($"Error during publish message execution: {ex.ToString()}");
                    var pme = ex as PublishMessageException;
                    if(pme != null)
                    {
                        _logger.LogError($"Publish message exception details: {pme?.ExceptionDetails?.FirstOrDefault()?.Value}");
                    }
                    throw;
                }
            }
        }

        /// <summary>
        /// Creates a batch of messages to be sent to the specified endpoint
        /// </summary>
        /// <param name="publishMessageEventArgs"></param>
        /// <exception cref="VerificationException"></exception>
        private void BatchMessage(PublishMessageEventArgs publishMessageEventArgs)
        {
            var sessionId = publishMessageEventArgs.SessionId;
            List<PublishMessageEventArgs> eventArgs = null;
            if (_messageBatchSessionEntries.ContainsKey(sessionId))
            {
                eventArgs = _messageBatchSessionEntries[sessionId];
            }
            else
            {
                eventArgs = new List<PublishMessageEventArgs>();
                var added = _messageBatchSessionEntries.TryAdd(sessionId, eventArgs);
                if (!added) { throw new VerificationException("Error adding entry for batch", new Details { Code = "", Name = "", Value = "" }); }
            }

            if (!publishMessageEventArgs.IsEndRequest)
            {
                eventArgs.Add(publishMessageEventArgs);
            }

            if (eventArgs.Count >= 1000 || publishMessageEventArgs.IsEndRequest) // send batch
            {
                SendBatch(eventArgs, sessionId);
                _messageBatchSessionEntries[sessionId].Clear();

            }
        }

        /// <summary>
        /// Sends a batch of messages to the specified endpoint
        /// </summary>
        /// <param name="eventArgs"></param>
        /// <param name="sessionId"></param>
        private void SendBatch(List<PublishMessageEventArgs> eventArgs, string sessionId) 
        {
            var response = new VerificationMessageResponse();
            response.SessionId = sessionId;
            response.Messages = new List<VerificationMessage>();
            if (eventArgs.Count == 0)
                return;
            var publishMessageEndpoint = eventArgs[0].PublishMessageEndpoint;
            var publishMessageRequestId = eventArgs[0].RequestId;
            var publishMessageTraceId = eventArgs[0].TraceId;

            foreach (var eventArg in eventArgs)
            {
                response.Messages.Add(eventArg.Message);
            }

            _verificationServiceClient.PublishVerificationMessages(publishMessageEndpoint, response, publishMessageRequestId, publishMessageTraceId, new CancellationToken()).GetAwaiter().GetResult();
        }

        /// <summary>
        /// Logs the error and exception details
        /// </summary>
        /// <param name="errors"></param>
        /// <param name="ex"></param>
        private void LogError(List<ErrorModel> errors, Exception ex)
        {
            var addonException = ex as AddonException;
            var error = new ErrorModel { ErrorMessage = ex.Message, Details = addonException?.ExceptionDetails ?? new Details[] { new Details { Code = "AEM.Job.Error", Name = "AEM Job", Value = ex.Message } } };
            errors.Add(error);
            if (addonException != null)
            {
                var exceptionMessage = $"{addonException.Message}:({addonException.ErrorCode}):{addonException?.ExceptionDetails[0]?.Value}";
                // Log AddonException here based on LoggingLevel in exception
                _logger.LogError(exceptionMessage);
            }
            else
            {
                _logger.LogError(error.ErrorMessage);
            }
        }

        /// <summary>
        /// Logs the request details
        /// </summary>
        /// <param name="request"></param>
        private void LogRequestDetails(VerificationRequest request)
        {
            _logger.LogInformation($"VerificationSessionId: {request.SessionId}," +
                $" NativeAnnotatedFileUrl: {request?.InputResourceDetails?.NativeAnnotatedFileUrl}" +
                $" BCM download URl: {request?.InputResourceDetails?.BilingualDocumentUrl}" +
                $" NativeResourcePackageUrl: {request?.InputResourceDetails?.VerificationResourcePackageUrl}" +
                $" PublishMessageUrl: {request?.PublishMessageUrl}" +
                $", CallbackUrl: {request.CallbackUrl}");
        }

        /// <summary>
        /// Validates the request - change accordingly
        /// </summary>
        /// <param name="request"></param>
        /// <exception cref="VerificationRequestValidationException"></exception>
        private void ValidateRequest(VerificationRequest request)
        {
            // Validation as needed on the request fields... To Be Defined more (Code/Name/Value)
            var errorDetails = new List<Details>();
            if (string.IsNullOrEmpty(request.SessionId) || string.IsNullOrWhiteSpace(request.SessionId))
            {
                errorDetails.Add(new Details { Code = "ExternalVerificationSessionIdMissing", Name = "", Value = "" });
            }

            if(errorDetails.Count > 0)
            {
                throw new VerificationRequestValidationException("Error", errorDetails.ToArray());
            }
        }

    }
}
