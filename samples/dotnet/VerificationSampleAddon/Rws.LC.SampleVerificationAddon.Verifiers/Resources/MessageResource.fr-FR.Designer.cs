﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:4.0.30319.42000
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Rws.LC.SampleVerificationAddon.Verifiers.Resources {
    using System;
    
    
    /// <summary>
    ///   A strongly-typed resource class, for looking up localized strings, etc.
    /// </summary>
    // This class was auto-generated by the StronglyTypedResourceBuilder
    // class via a tool like ResGen or Visual Studio.
    // To add or remove a member, edit your .ResX file then rerun ResGen
    // with the /str option, or rebuild your VS project.
    [global::System.CodeDom.Compiler.GeneratedCodeAttribute("System.Resources.Tools.StronglyTypedResourceBuilder", "17.0.0.0")]
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute()]
    [global::System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    public class MessageResource___Copy__2_ {
        
        private static global::System.Resources.ResourceManager resourceMan;
        
        private static global::System.Globalization.CultureInfo resourceCulture;
        
        [global::System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
        internal MessageResource___Copy__2_() {
        }
        
        /// <summary>
        ///   Returns the cached ResourceManager instance used by this class.
        /// </summary>
        [global::System.ComponentModel.EditorBrowsableAttribute(global::System.ComponentModel.EditorBrowsableState.Advanced)]
        public static global::System.Resources.ResourceManager ResourceManager {
            get {
                if (object.ReferenceEquals(resourceMan, null)) {
                    global::System.Resources.ResourceManager temp = new global::System.Resources.ResourceManager("Rws.LC.SampleVerificationAddon.Verifiers.Resources.MessageResource - Copy (2)", typeof(MessageResource___Copy__2_).Assembly);
                    resourceMan = temp;
                }
                return resourceMan;
            }
        }
        
        /// <summary>
        ///   Overrides the current thread's CurrentUICulture property for all
        ///   resource lookups using this strongly typed resource class.
        /// </summary>
        [global::System.ComponentModel.EditorBrowsableAttribute(global::System.ComponentModel.EditorBrowsableState.Advanced)]
        public static global::System.Globalization.CultureInfo Culture {
            get {
                return resourceCulture;
            }
            set {
                resourceCulture = value;
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Target Segment with ID {0} is longer than source segment by {1} characters.
        /// </summary>
        public static string MessageLengthExceeded_DetailedDescription {
            get {
                return ResourceManager.GetString("MessageLengthExceeded_DetailedDescription", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Target Segment with ID {0} is longer than source segment by {1} characters.
        /// </summary>
        public static string MessageLengthExceeded_EM {
            get {
                return ResourceManager.GetString("MessageLengthExceeded_EM", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Target segment exceeds source segment length.
        /// </summary>
        public static string MessageLengthExceeded_FriendlyName {
            get {
                return ResourceManager.GetString("MessageLengthExceeded_FriendlyName", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Remove {1} characters from target segment.
        /// </summary>
        public static string MessageLengthExceeded_Suggestion {
            get {
                return ResourceManager.GetString("MessageLengthExceeded_Suggestion", resourceCulture);
            }
        }
    }
}
