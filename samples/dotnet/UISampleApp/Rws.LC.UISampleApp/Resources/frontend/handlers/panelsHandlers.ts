import { ExtensibilityEventDetail } from "@trados/trados-ui-extensibility";
import { logExtensionData } from "./helpers";

/**
 * Handles myCustomSidebarBox's render event.
 * Adds HTML content to the sidebarBox.
 *
 * @param detail The event detail object.
 */
export const myCustomSidebarBoxRendered = (
    detail: ExtensibilityEventDetail
) => {
    logExtensionData(detail);

    const sidebarBoxContentWrapper = document.getElementById(detail.domElementId);
    if (sidebarBoxContentWrapper) {
        // Reset content for re-renders: as the state changes in the Trados UI depending on user actions, re-renders occur.
        sidebarBoxContentWrapper.innerHTML = "";

        // Create and append div.
        const div = document.createElement("div");
        div.innerHTML = `Custom sidebar box content inserted on render.`;
        sidebarBoxContentWrapper.appendChild(div);
    }
};

/**
 * Handles myCustomPanel's render event.
 * Adds HTML content to the panel.
 *
 * @param detail The event detail object.
 */
export const myCustomPanelRendered = (
    detail: ExtensibilityEventDetail
) => {
    logExtensionData(detail);
    
    const panelContentWrapper = document.getElementById(detail.domElementId);
    if (panelContentWrapper) {
        // Reset content for re-renders: as the state changes in the Trados UI depending on user actions, re-renders occur.
        panelContentWrapper.innerHTML = "";

        // Create and append div.
        const div = document.createElement("div");
        div.innerHTML = `Custom panel content inserted on render.`;
        panelContentWrapper.appendChild(div);
    }
};

/**
 * Handles myCustomTab's render event.
 * Adds HTML content to the tab.
 *
 * @param detail The event detail object.
 */
export const myCustomTabRendered = (
    detail: ExtensibilityEventDetail
) => {
    logExtensionData(detail);

    const tabContentWrapper = document.getElementById(detail.domElementId);
    if (tabContentWrapper) {
        // Reset content for re-renders: as the state changes in the Trados UI depending on user actions, re-renders occur.
        tabContentWrapper.innerHTML = "";

        // Create and append div.
        const div = document.createElement("div");
        div.innerHTML = `Custom tab content inserted on render.`;
        tabContentWrapper.appendChild(div);
    }
};
