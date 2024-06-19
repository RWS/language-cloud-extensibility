import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { logExtensionData } from "./helpers";

let renderCount = 0;

export const taskTabRendered = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  renderCount++;

  // custom-tab is the elementId of the tab (in activated-extension.json config)
  const tabContentWrapper = document.getElementById(detail.domElementId);
  if (tabContentWrapper) {
    // reset content for rerenders
    tabContentWrapper.innerHTML = "";

    // create heading
    const heading = document.createElement("div");
    heading.className = "x-panel-header-title-light-framed";
    heading.setAttribute("style", "margin-bottom: 10px");
    heading.innerHTML = `Custom tab rendered <b>${renderCount}</b> times.`;

    //task details selector set as event payload
    const task = detail.task;
    console.log("[UI Extensibility] [extension] Task from event", task);
    // add heading to tab
    tabContentWrapper.appendChild(heading);
    tabContentWrapper.style.position = "relative";
  }
};
