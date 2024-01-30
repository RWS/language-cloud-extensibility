using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Rws.LanguageCloud.Authentication.Jws;
using Rws.LC.SampleVerificationAddon.DAL;
using Rws.LC.SampleVerificationAddon.Exceptions;
using Rws.LC.SampleVerificationAddon.Helpers;
using Rws.LC.SampleVerificationAddon.Interfaces;
using Rws.LC.SampleVerificationAddon.Services;
using Rws.LC.SampleVerificationAddon.RestService.Helpers;
using Rws.LC.SampleVerificationAddon.RestService.Interfaces;
using Rws.LC.SampleVerificationAddon.RestService.Services;
using Rws.LC.SampleVerificationAddon.Verifiers.Other;
using Rws.LC.SampleVerificationAddon.Verifiers.Interfaces;
using System.Net;
using System.Text.Json;
using System.Text.Json.Serialization;
using Rws.VerificationSampleAddon.RestService.Exceptions;

namespace Rws.LC.SampleVerificationAddon
{
    public class Startup
	{
		public Startup(IConfiguration configuration)
		{
			Configuration = configuration;
		}

		public IConfiguration Configuration { get; }

		// This method gets called by the runtime. Use this method to add services to the container.
		public void ConfigureServices(IServiceCollection services)
		{
			services.AddControllers().AddJsonOptions(opts =>
			{
				// only for system.text
				opts.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());
				opts.JsonSerializerOptions.DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull;
				opts.JsonSerializerOptions.PropertyNameCaseInsensitive = true;
				opts.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
			});

			services.AddSingleton<IDatabaseContext, DatabaseContext>();
			services.AddSingleton<IHealthReporter, HealthReporter>();
			services.AddSingleton<IRepository, Repository>();
			services.AddSingleton<IDescriptorService, DescriptorService>();
			services.AddSingleton<IAccountService, AccountService>();

            services.AddSingleton<IVerificationService, VerificationService>();

            services.AddHostedService<QueuedHostedService>();
            services.AddSingleton<IBackgroundTaskQueue, BackgroundTaskQueue>();

            services.AddSingleton<IBcmServiceClient, BcmServiceClient>();
            services.AddSingleton<IVerificationServiceClient, VerificationServiceClient>();
            services.AddSingleton<IFileManagementServiceClient, FileManagementServiceClient>();
            services.AddSingleton<IExternalJobServiceClient, ExternalJobServiceClient>();
            services.AddSingleton<IVerifierFactory, VerifierFactory>();

            services.AddAuthentication(JwsDefaults.AuthenticationScheme)
				.AddJws(options =>
				{
					//that is not implemented yet:
					//options.TokenValidationParameters.RequireSignedTokens = Configuration["ASPNETCORE_ENVIRONMENT"] != "Development";
					options.JwksUri = Configuration["Authorization:JwksUri"];
					options.TokenValidationParameters.ValidIssuer = Configuration["Authorization:Issuer"];
					options.TokenValidationParameters.ValidAudience = Configuration["baseUrl"];
				});
		}

		// This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
		public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
		{
			if (env.IsDevelopment())
			{
				app.UseDeveloperExceptionPage();
			}

			// This is required for intercepting the request body in validating the request signatures
			app.Use(next => context => {
				context.Request.EnableBuffering();
				return next(context);
			});

			app.UseHttpsRedirection();

			app.UseExceptionHandler(appError =>
			{
				appError.Run(async context =>
				{
					context.Response.StatusCode = (int)HttpStatusCode.InternalServerError;
					context.Response.ContentType = "application/json";

					var contextFeature = context.Features.Get<IExceptionHandlerFeature>();
					if (contextFeature != null)
					{
						string message;

						if (contextFeature.Error is AddonException)
						{
							var exception = (AddonException)contextFeature.Error;

							context.Response.StatusCode = (int)exception.StatusCode;
							message = JsonSerializer.Serialize(new
							{
								message = exception.Message,
								errorCode = exception.ErrorCode,
								details = exception.ExceptionDetails
							}, JsonSettings.Default());
						}
						else
						{
							message = JsonSerializer.Serialize(new
							{
								errorCode = ErrorCodes.InternalError,
								message = contextFeature.Error.Message,
							}, JsonSettings.Default());
						}

						await context.Response.WriteAsync(message).ConfigureAwait(false);
					}
				});
			});

			app.UseRouting();

			app.UseAuthentication();
			app.UseAuthorization();

			app.UseEndpoints(endpoints =>
			{
				endpoints.MapControllers();
			});

		}
	}
}
