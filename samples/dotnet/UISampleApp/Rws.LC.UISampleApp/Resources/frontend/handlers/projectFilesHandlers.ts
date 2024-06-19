import { trados } from "@sdl/extensibility"; // doesn't work until new extensibility public package published
//import { trados } from "../../../../../../../public-packages/extensibility/src/index"; // works
import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { downloadData, logExtensionData } from "./helpers";

const setProgressRowDisplayStyle = (displayStyle: string) => {
  const progressTexts = document.getElementsByClassName("x-progress-text");
  for (let i = 0; i < progressTexts.length; i++) {
    if (progressTexts[i].textContent === "0%") {
      const expiredRow = progressTexts[i].closest("tr");
      if (expiredRow) {
        //@ts-ignore
        expiredRow.style.display = displayStyle;
      }
    }
  }
};

export const getLocalFilesDataButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  trados
    .getLocalData(trados.contexts.projects, trados.dataSelectors.projectFiles)
    .then(downloadData)
    .catch(_reason => {
      debugger; // todo
    });
};

export const hideNotTranslatedFilesButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  setProgressRowDisplayStyle("none");
};

export const showAllFilesButtonClicked = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  setProgressRowDisplayStyle("table-row");
};
