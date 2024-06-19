import { trados } from "@sdl/extensibility"; // doesn't work until new extensibility public package published
//import { trados } from "../../../../../../../public-packages/extensibility/src/index"; // works
import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { downloadData, logExtensionData } from "./helpers";

export const dashboardSidebarBoxRendered = (
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
    boxContentWrapper.appendChild(div);
  }
};

export const getLocalDashboardDataButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  trados
    .getLocalData(
      trados.contexts.projects,
      trados.dataSelectors.projectDashboard
    )
    .then(downloadData)
    .catch(_reason => {
      debugger; // todo
    });
};

export const increaseFontSizeButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const panels = document.getElementsByTagName("table");
  for (let i = 0; i < panels.length; i++) {
    panels[i].style.fontSize = "20px";
  }
};

export const backToDefaultFontSizeButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const panels = document.getElementsByTagName("table");
  for (let i = 0; i < panels.length; i++) {
    panels[i].style.fontSize = "initial";
  }
};

export const dashboardMainPanelRendered = (
  detail: ExtensibilityEventDetail,
  panelIndex: number
) => {
  logExtensionData(detail);

  // extensionHelper is the elementId of the tab (in activated-extension.json config)
  const panelContentWrapper = document.getElementById(detail.domElementId);
    if (panelContentWrapper) {
      // reset content for rerenders
      panelContentWrapper.innerHTML = "";   // todo: fix multiple renders
      // create div
      const div = document.createElement("div");
      div.innerHTML = `Custom panel <b>${panelIndex}</b> content inserted on render.<br/>This panel was added to column <b>${
        !isNaN(panelIndex) && panelIndex % 2 ? "1" : "2"
      }</b> in Dashboard's main section.`;
      panelContentWrapper.appendChild(div);
  }
};
