﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rws.LC.VerificationSampleApp.Verifiers.Interfaces
{
    public interface INativeFileVerifier : IVerifier
    {
        public Task Verify(string nativeFilePath);
    }
}
