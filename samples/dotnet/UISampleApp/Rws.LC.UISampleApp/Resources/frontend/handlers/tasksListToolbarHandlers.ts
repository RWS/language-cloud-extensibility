import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { logExtensionData } from "./helpers";

let selectedNewTasks: undefined | any[] = undefined;
let selectedActiveTasks: undefined | any[] = undefined;
let selectedCompletedTasks: undefined | any[] = undefined;

export const newTasksListToolbarButtonRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const button = document.getElementById(detail.domElementId);
  selectedNewTasks = detail.selectedNewTasks;
  if (button && selectedNewTasks) {
    console.log("RENDERED NEW");
  }
};

export const newTasksListToolbarButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  if (detail.selectedNewTasks) {
    selectedNewTasks = detail.selectedNewTasks;
    console.log("CLICKED NEW", selectedNewTasks);
  }
};

export const activeTasksListToolbarButtonRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const button = document.getElementById(detail.domElementId);
  selectedActiveTasks = detail.selectedActiveTasks;
  if (button && selectedActiveTasks) {
    console.log("RENDERED ACTIVE");
  }
};

export const activeTasksListToolbarButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  if (detail.selectedActiveTasks) {
    selectedActiveTasks = detail.selectedActiveTasks;
    console.log("CLICKED ACTIVE", selectedActiveTasks);
  }
};

export const completedTasksListToolbarButtonRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const button = document.getElementById(detail.domElementId);
  selectedCompletedTasks = detail.selectedCompletedTasks;
  if (button && selectedCompletedTasks) {
    console.log("RENDERED COMPLETED");
  }
};

export const completedTasksListToolbarButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  if (detail.selectedCompletedTasks) {
    selectedCompletedTasks = detail.selectedCompletedTasks;
    console.log("CLICKED COMPLETED", selectedCompletedTasks);
  }
};
