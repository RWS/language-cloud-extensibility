import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { Task } from "@sdl/extensibility/dist/lib/lc-public-api/models";

import { logExtensionData } from "./helpers";

let taskPreview: Task | undefined = undefined;

export const taskSidebarBoxRendered = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  const boxContentWrapper: HTMLElement | null = document.getElementById(
    detail.domElementId
  );
  taskPreview =
    detail.newTaskPreview ||
    detail.activeTaskPreview ||
    detail.completedTaskPreview ||
    undefined; // reset previous taskPreview?

  console.log(
    "[UI Extensibility] [extension] Update preview sidebar box",
    boxContentWrapper,
    taskPreview
  );

  if (boxContentWrapper && taskPreview) {
    const filename = taskPreview.inputFiles?.length
      ? taskPreview.inputFiles[0].sourceFile?.name
      : "";

    // version 1: create html element and add to DOM
    boxContentWrapper.innerHTML = "";
    // create div
    const div = document.createElement("div");
    div.innerHTML = `Custom sidebar box content inserted on render.<br/>Task description: ${
      filename ? filename : "no inputFiles"
    }`;
    boxContentWrapper.appendChild(div);
    /*
      // version 2: don't create html element and add to DOM; just change innerHTML
      boxContentWrapper.innerHTML = `Custom sidebar box content inserted on render.<br/>Task description: ${
        filename ? filename : "no filename in metadata"
      }`;
      */
  } else {
    console.error(
      `[UI Extensibility] [extension] No boxContentWrapper or taskPreview data`
    );
  }
};
