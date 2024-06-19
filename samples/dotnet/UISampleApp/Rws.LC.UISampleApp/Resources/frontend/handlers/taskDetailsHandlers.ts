import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { logExtensionData } from "./helpers";

// sidebar box (all tabs, not just Details tab)
export const taskDetailsSidebarBoxRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);

  const task = detail.task;
  console.log("[UI Extensibility] [extension] Task data", task);

  // extensionHelper is the elementId of the tab (in activated-extension.json config)
  const boxContentWrapper = document.getElementById(detail.domElementId);
  if (boxContentWrapper) {
    // reset content for rerenders
    boxContentWrapper.innerHTML = "";
    // create div
    const div = document.createElement("div");
    div.innerHTML = `Custom sidebar box content inserted on render.<br/>${
      task ? task.id : "No task id available."
    }`;
    boxContentWrapper.appendChild(div);
  }
};

export const taskDetailsToolbarButtonRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const button = document.getElementById(detail.domElementId);
  const task = detail.task;
  if (button && task) {
    console.log("RENDERED DETAILS TOOLBAR BUTTON");
  }
};

export const taskDetailsToolbarButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const task = detail.task;
  console.log("CLICKED DETAILS TOOLBAR BUTTON", task);
};

// panel rendered
export const detailsMainBoxRendered = (
  detail: ExtensibilityEventDetail,
  panelIndex: number
) => {
  logExtensionData(detail);

  // extensionHelper is the elementId of the tab (in activated-extension.json config)
  const panelContentWrapper = document.getElementById(detail.domElementId);
  if (panelContentWrapper) {
    // reset content for rerenders
    panelContentWrapper.innerHTML = "";

    // create div
    const div = document.createElement("div");
    div.innerHTML = `Custom panel <b>${panelIndex}</b> content inserted on render.`;
    panelContentWrapper.appendChild(div);
  }
};

// files tab
export const taskFilesToolbarButtonRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const button = document.getElementById(detail.domElementId);
  const task = detail.task;
  if (button && task) {
    console.log("RENDERED FILES TOOLBAR BUTTON");
  }
};

export const taskFilesToolbarButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const task = detail.task;
  const selectedFiles = detail.selectedFiles;
  console.log("CLICKED FILES TOOLBAR BUTTON", task, selectedFiles);
};
