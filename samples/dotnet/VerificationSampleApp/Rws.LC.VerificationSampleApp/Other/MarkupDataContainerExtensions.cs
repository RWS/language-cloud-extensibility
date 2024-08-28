using Sdl.Core.Bcm.BcmModel;
using System;

namespace Rws.LC.VerificationSampleApp.Verifiers.Other
{
    public static class MarkupDataContainerExtensions
    {
        public static void ForEach(this MarkupDataContainer container, Action<MarkupData> action)
        {
            foreach (var markupData in container.Children)
            {
                action(markupData);
            }
        }
    }
}
