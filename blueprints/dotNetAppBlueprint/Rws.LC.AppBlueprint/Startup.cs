using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using MongoDB.Bson.Serialization;
using Rws.LanguageCloud.Authentication.Jws;
using Rws.LC.AppBlueprint.DAL;
using Rws.LC.AppBlueprint.Exceptions;
using Rws.LC.AppBlueprint.Helpers;
using Rws.LC.AppBlueprint.Interfaces;
using Rws.LC.AppBlueprint.Services;
using System.Net;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace Rws.LC.AppBlueprint
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
            services.AddSingleton<IAppRegistrationRepository, AppRegistrationRepository>();
            services.AddSingleton<IDescriptorService, DescriptorService>();
            services.AddSingleton<IAccountService, AccountService>();

            services.AddAuthentication(JwsDefaults.AuthenticationScheme)
                .AddJws(options =>
                {
                    //that is not implemented yet:
                    //options.TokenValidationParameters.RequireSignedTokens = Configuration["ASPNETCORE_ENVIRONMENT"] != "Development";
                    options.JwksUri = Configuration["Authorization:JwksUri"];
                    options.TokenValidationParameters.ValidIssuer = Configuration["Authorization:Issuer"];
                    options.TokenValidationParameters.ValidAudience = Configuration["baseUrl"];
                });

            BsonSerializer.RegisterSerializationProvider(new JsonNodeSerializerProvider());
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            // This is required for intercepting the request body in validating the request signatures
            app.Use(next => context =>
            {
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

                        if (contextFeature.Error is AppException)
                        {
                            var exception = (AppException)contextFeature.Error;

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
