using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Primitives;
using NLog.Web;
using NLog.Web.LayoutRenderers;
using System;
using System.Linq;

namespace Rws.LC.SampleVerificationAddon
{
    public class Program
    {
        public static void Main(string[] args)
        {
            AspNetLayoutRendererBase.Register("TR_ID", (logEventInfo, httpContext, loggingConfiguration) =>
            {
                StringValues traceValues;
                if (httpContext == null) return null;
                if (!httpContext.Request.Headers.TryGetValue("TR_ID", out traceValues)) return null;
                return traceValues.FirstOrDefault();
            });

            var logger = NLogBuilder.ConfigureNLog("nlog.config").GetCurrentClassLogger();
            try
            {
                logger.Debug("init main");
                CreateHostBuilder(args).Build().Run();
            }
            catch (Exception exception)
            {
                //NLog: catch setup errors
                logger.Error(exception, "Stopped program because of exception");
                throw;
            }
            finally
            {
                // Ensure to flush and stop internal timers/threads before application-exit (Avoid segmentation fault on Linux)
                NLog.LogManager.Shutdown();
            }
        }

        public static IHostBuilder CreateHostBuilder(string[] args) =>
            Host.CreateDefaultBuilder(args)
                .ConfigureWebHostDefaults(webBuilder =>
                {
                    webBuilder.UseStartup<Startup>();
                })
                .ConfigureLogging(logging =>
                {
                    logging.ClearProviders();
                    logging.SetMinimumLevel(LogLevel.Trace);
                })
                .UseNLog();
    }
}
