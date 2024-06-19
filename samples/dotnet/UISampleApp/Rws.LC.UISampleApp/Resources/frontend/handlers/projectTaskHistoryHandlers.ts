import { trados } from "@sdl/extensibility"; // doesn't work until new extensibility public package published
//import { trados } from "../../../../../../../public-packages/extensibility/src/index"; // works
import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { downloadData, logExtensionData } from "./helpers";

const setExpiredRowDisplayStyle = (displayStyle: string) => {
  const statusText = document.getElementsByClassName("status file-failed");
  for (let i = 0; i < statusText.length; i++) {
    const expiredRow = statusText[i].closest("tr");
    if (expiredRow) {
      //@ts-ignore
      expiredRow.style.display = displayStyle;
    }
  }
};

export const getLocalTaskHistoryDataButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  trados
    .getLocalData(
      trados.contexts.projects,
      trados.dataSelectors.projectTaskHistory
    )
    .then(downloadData)
    .catch(_reason => {
      debugger; // todo
    });
};

export const hideFailedTasksButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  setExpiredRowDisplayStyle("none");
};

export const showAllTasksButtonClicked = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  setExpiredRowDisplayStyle("table-row");
};

export const highlightSelectedTaskButtonRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  //const isPreviewPanelOpen = detail.projectTaskHistory!.preview.show;
  // todo: check if closing preview makes selectedTaskHistoryTask null
  const isPreviewPanelOpen = Boolean(detail.selectedTaskHistoryTask);

  // update button availability
  trados.updateElement("highlightSelectedTaskButton", {
    disabled: !isPreviewPanelOpen
  });
  const selectedTaskRows = document.getElementsByClassName("x-grid-row");
  //@ts-ignore
  for (let selectedTaskRow of selectedTaskRows) {
    //@ts-ignore
    if (selectedTaskRow && selectedTaskRow.style)
      //@ts-ignore
      selectedTaskRow.style.backgroundColor = "initial";
  }
};

export const highlightSelectedTaskButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const selectedTaskRows = document.getElementsByClassName(
    " x-grid-row x-grid-row-active"
  );
  if (selectedTaskRows) {
    //@ts-ignore
    selectedTaskRows[0].style.backgroundColor = "#1d9570";
  }
};

// task history sidebar box
export const taskHistorySidebarBoxRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  // extensionHelper is the elementId of the tab (in activated-extension.json config)
  const boxContentWrapper = document.getElementById(detail.domElementId);
  if (boxContentWrapper) {
    boxContentWrapper.innerHTML = "";
    // create div
    const div = document.createElement("div");
    div.innerHTML = `Custom sidebar box content inserted on render.`;
    if (detail.selectedTaskHistoryTask?.status) {
      div.innerHTML += `<br/><b>Status: ${detail.selectedTaskHistoryTask.status}</b>`;
    }
    boxContentWrapper.appendChild(div);
  }
};
