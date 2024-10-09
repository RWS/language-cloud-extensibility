import { ExtensibilityEventDetail } from "@trados/trados-ui-extensibility";

/**
 * Logs the event detail object to the console.
 *
 * @param detail The event detail object.
 */
export const logExtensionData = (detail: ExtensibilityEventDetail) => {
  console.log("[UI Extensibility] [my UI extension] Custom event detail", detail);
};
