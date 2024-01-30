using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.SampleVerificationAddon.Verifiers.Settings
{
    public class SourceTargetLengthVerifierSettings
    {
        public bool Enabled {  get; set; }
        public VerificationResourcePackage VerificationResourcePackage { get; set; }
        public int LengthCheckCharacterLimit { get; set; }

    }
}
