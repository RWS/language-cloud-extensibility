﻿using System.Collections.Generic;

namespace Rws.LC.SampleVerificationAddon.RestService.Models
{
    public class MessageTypeResponse
    {
        public string Culture { get; set; }
        public List<MessageTypeLocalizationData> MessageTypes { get; set; }
    }
}
