import { trados } from "@sdl/extensibility"; // doesn't work until new extensibility public package published
//import { trados } from "../../../../../../../public-packages/extensibility/src/index"; // works
import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { downloadData, logExtensionData } from "./helpers";

export const getLocalStagesDataButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  trados
    .getLocalData(trados.contexts.projects, trados.dataSelectors.projectStages)
    .then(downloadData)
    .catch(_reason => {
      debugger; // todo
    });
};

export const highlightPopulatedStagesButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const panels = document.getElementsByClassName("items-count");
  for (let i = 0; i < panels.length; i++) {
    if (panels[i].textContent !== "0") {
      //@ts-ignore
      panels[i].style.background = "green";
    }
  }
};

export const removeStagesHighlightsButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const panels = document.getElementsByClassName("items-count");
  for (let i = 0; i < panels.length; i++) {
    if (panels[i].textContent !== "0") {
      //@ts-ignore
      panels[i].style.background = "#9ba9b6";
    }
  }
};
