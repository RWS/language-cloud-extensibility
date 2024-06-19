import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import {
  currentProject,
  projectImportanceList
} from "./projectToolbarHandlers";

export const logExtensionData = (detail: ExtensibilityEventDetail) => {
  console.log("[UI Extensibility] [extension] Custom event detail", detail);
};

// simple file download
// no longer used for initial "Create invoice" button; still used for "Get local data" button
export const download = (filename: string, data: string | Blob) => {
  const element = document.createElement("a");
  if (typeof data === "string") {
    element.href = "data:text/plain;charset=utf-8," + encodeURIComponent(data);
  } else {
    element.href = URL.createObjectURL(data);
  }
  element.setAttribute("download", filename);
  element.style.display = "none";
  document.body.appendChild(element);
  element.click();
  document.body.removeChild(element);
};

export const downloadData = (data: any) => {
  const filename = "local-data.txt";
  console.log(
    `[UI Extensibility] [extension] Download data as ${filename} file`,
    data
  );

  setTimeout(() => {
    download(filename, JSON.stringify(data));
  }, 1);
};

export const updateProjectImportanceList = (data?: any) => {
    const currentProjectImportanceItem = projectImportanceList.find(
        i => i.projectId === currentProject!.id
    );
    if (currentProjectImportanceItem) {
        if (data.responseData.importance) {
            // update existing entry
            currentProjectImportanceItem.importance =
                data.responseData.importance.toLowerCase();
            if (currentProjectImportanceItem.pending) {
                currentProjectImportanceItem.pending = false;
                currentProjectImportanceItem.id = data.responseData.id;
            }
        } else {
            // delete entry
            projectImportanceList.splice(
                projectImportanceList.indexOf(currentProjectImportanceItem),
                1
            );
        }
    } else {
        // add entry
        projectImportanceList.push({
            id: data.responseData.id,
            projectId: data.responseData.projectId,
            importance: data.responseData.importance,
            pending: false
        });
    }
};

// a similar function exists in public-packages/extensibility/src/api/apiClient.ts but is intentionally not exported
// this helper function in the mock extension is used when calling add-on/app backend api directly, see api/project-metadata in projects-app/src/mocks/ui-extensibility/extensions/handlers/projectToolbarHandlers.ts
export const getEnvironmentUrlPartByHost = () => {
    const hostEnv = document.location.host.split("-")[0];
    switch (hostEnv) {
        case "ci":
        case "qa":
        case "pte":
        case "uat":
        case "staging":
            return `${hostEnv}-`;
        default:
            return ""; // prod
    }
};