import { trados } from "@sdl/extensibility"; // doesn't work until new extensibility public package published
//import { trados } from "../../../../../../../public-packages/extensibility/src/index"; // works
import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { downloadData, logExtensionData } from "./helpers";

export const getLocalSelectedProjectsDataButtonRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const button = document.getElementById(detail.domElementId);
  const selectedProjects = detail.selectedProjects;
  if (button && selectedProjects) {
    // update button disabled state
    trados.updateElement("getLocalSelectedProjectsDataButton", {
      disabled: selectedProjects.length === 0
    });
  }
};

export const getLocalSelectedProjectsDataButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const selectedProjects = detail.selectedProjects;
  if (selectedProjects && selectedProjects.length) {
    trados
      .getLocalData(
        trados.contexts.projects,
        trados.dataSelectors.selectedProjects
      )
      .then(downloadData)
      .catch(_reason => {
        debugger; // todo
      });
  } else {
    trados.showNotification(
      "No selected projects",
      trados.contexts.projects,
      trados.notificationTypes.warning
    );
  }
};
