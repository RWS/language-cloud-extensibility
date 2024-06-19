import { trados } from "@sdl/extensibility"; // doesn't work until new extensibility public package published
//import { trados } from "../../../../../../../public-packages/extensibility/src/index"; // works
import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { download, logExtensionData } from "./helpers";
import { TargetFile } from "@sdl/extensibility/lib/lc-public-api/models";

export const compareTaskApiToLocalDataButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const clickedSelector = detail.value; // extensibilityComponents sets value in menu item handler, so selector string

  if (clickedSelector) {
    switch (clickedSelector) {
      case "selectedNewTasks":
        const newTasks = detail.selectedNewTasks;
        if (newTasks?.length) {
          // notification
          trados.showNotification(
            `Getting data via Public API for ${newTasks.length} selected new task(s)`,
            trados.contexts.taskInbox,
            trados.notificationTypes.info
          );

          // task api call for every selected task
          Promise.all(
            newTasks.map(t =>
              trados.apiClient.taskApi().getTask({
                taskId: t.id,
                ...trados.getRegistrationResult(),
                fields:
                  "id,status,taskType,input,inputFiles,owner,assignees,dueBy,createdAt,outcome,comment,project,failedTask,completedAt"
              })
            )
          )
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.taskInbox,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(newTasks)
              );
            })
            .catch(e => {
              console.error(
                `[UI Extensibility] [extension] At least one API call failed`,
                e
              );
            });
        } else {
          // notification
          trados.showNotification(
            "No tasks selected in inbox new tasks tab",
            trados.contexts.taskInbox,
            trados.notificationTypes.warning
          );
        }
        break;
      case "selectedActiveTasks":
        const activeTasks = detail.selectedActiveTasks;
        if (activeTasks?.length) {
          // notification
          trados.showNotification(
            `Getting data via Public API for ${activeTasks.length} selected new task(s)`,
            trados.contexts.taskInbox,
            trados.notificationTypes.info
          );

          // task api call for every selected task
          Promise.all(
            activeTasks.map(t =>
              trados.apiClient.taskApi().getTask({
                taskId: t.id,
                ...trados.getRegistrationResult(),
                fields:
                  "id,status,taskType,input,inputFiles,owner,assignees,dueBy,createdAt,outcome,comment,project,failedTask,completedAt"
              })
            )
          )
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.taskInbox,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(activeTasks)
              );
            })
            .catch(e => {
              console.error(
                `[UI Extensibility] [extension] At least one API call failed`,
                e
              );
            });
        } else {
          // notification
          trados.showNotification(
            "No tasks selected in inbox active tasks tab",
            trados.contexts.taskInbox,
            trados.notificationTypes.warning
          );
        }
        break;
      case "selectedCompletedTasks":
        const completedTasks = detail.selectedCompletedTasks;
        if (completedTasks?.length) {
          // notification
          trados.showNotification(
            `Getting data via Public API for ${completedTasks.length} selected new task(s)`,
            trados.contexts.taskInbox,
            trados.notificationTypes.info
          );

          // task api call for every selected task
          Promise.all(
            completedTasks.map(t =>
              trados.apiClient.taskApi().getTask({
                taskId: t.id,
                ...trados.getRegistrationResult(),
                fields:
                  "id,status,taskType,input,inputFiles,owner,assignees,dueBy,createdAt,outcome,comment,project,failedTask,completedAt"
              })
            )
          )
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.taskInbox,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(completedTasks)
              );
            })
            .catch(e => {
              console.error(
                `[UI Extensibility] [extension] At least one API call failed`,
                e
              );
            });
        } else {
          // notification
          trados.showNotification(
            "No tasks selected in inbox completed tasks tab",
            trados.contexts.taskInbox,
            trados.notificationTypes.warning
          );
        }
        break;
      case "newTaskPreview":
        const newTaskPreview = detail.newTaskPreview;
        if (newTaskPreview) {
          // notification
          trados.showNotification(
            "Getting data via Public API",
            trados.contexts.taskInbox,
            trados.notificationTypes.info
          );

          // task api call
          trados.apiClient
            .taskApi()
            .getTask({
              taskId: newTaskPreview.id,
              ...trados.getRegistrationResult(),
              fields:
                "id,status,taskType,input,inputFiles,owner,assignees,dueBy,createdAt,outcome,comment,project,failedTask,completedAt"
            })
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.taskInbox,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(newTaskPreview)
              );
            })
            .catch(e => {
              console.error(
                `[UI Extensibility] [extension] API call failed`,
                e
              );
            });
        } else {
          // notification
          trados.showNotification(
            "No new task preview",
            trados.contexts.taskInbox,
            trados.notificationTypes.warning
          );
        }
        break;
      case "activeTaskPreview":
        const activeTaskPreview = detail.activeTaskPreview;
        if (activeTaskPreview) {
          // notification
          trados.showNotification(
            "Getting data via Public API",
            trados.contexts.taskInbox,
            trados.notificationTypes.info
          );

          // task api call
          trados.apiClient
            .taskApi()
            .getTask({
              taskId: activeTaskPreview.id,
              ...trados.getRegistrationResult(),
              fields:
                "id,status,taskType,input,inputFiles,owner,assignees,dueBy,createdAt,outcome,comment,project,failedTask,completedAt"
            })
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.taskInbox,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(activeTaskPreview)
              );
            })
            .catch(e => {
              console.error(
                `[UI Extensibility] [extension] API call failed`,
                e
              );
            });
        } else {
          // notification
          trados.showNotification(
            "No active task preview",
            trados.contexts.taskInbox,
            trados.notificationTypes.warning
          );
        }
        break;
      case "completedTaskPreview":
        const completedTaskPreview = detail.completedTaskPreview;
        if (completedTaskPreview) {
          // notification
          trados.showNotification(
            "Getting data via Public API",
            trados.contexts.taskInbox,
            trados.notificationTypes.info
          );

          // task api call
          trados.apiClient
            .taskApi()
            .getTask({
              taskId: completedTaskPreview.id,
              ...trados.getRegistrationResult(),
              fields:
                "id,status,taskType,input,inputFiles,owner,assignees,dueBy,createdAt,outcome,comment,project,failedTask,completedAt"
            })
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.taskInbox,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(completedTaskPreview)
              );
            })
            .catch(e => {
              console.error(
                `[UI Extensibility] [extension] API call failed`,
                e
              );
            });
        } else {
          // notification
          trados.showNotification(
            "No completed task preview",
            trados.contexts.taskInbox,
            trados.notificationTypes.warning
          );
        }
        break;
      case "task":
        const task = detail.task;
        if (task) {
          // notification
          trados.showNotification(
            "Getting data via Public API",
            trados.contexts.taskInbox,
            trados.notificationTypes.info
          );

          // task api call
          trados.apiClient
            .taskApi()
            .getTask({
              taskId: task.id,
              ...trados.getRegistrationResult(),
              fields:
                "id,status,taskType,input,inputFiles,owner,assignees,dueBy,createdAt,outcome,comment,project,failedTask,completedAt"
            })
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.taskInbox,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(task)
              );
            })
            .catch(e => {
              console.error(
                `[UI Extensibility] [extension] API call failed`,
                e
              );
            });
        }
        break;
      case "selectedFiles":
        const files = detail.selectedFiles; // if file has status property, call targetFileApi instead of sourceFileApi
        const taskProject = detail.task?.project;
        if (taskProject && files?.length) {
          // notification
          trados.showNotification(
            "Getting data via Public API",
            trados.contexts.taskInbox,
            trados.notificationTypes.info
          );

          // file api call for every selected file
          Promise.all(
            files.map(f => {
              const fileIsTargetFile = Boolean((f as TargetFile).status); // todo: detect source vs target from selector data (only id, name, status properties)
              if (fileIsTargetFile) {
                // target file
                return trados.apiClient.targetFileApi().getTargetFile({
                  projectId: taskProject.id,
                  targetFileId: f.id,
                  ...trados.getRegistrationResult(),
                  fields:
                    "id,name,languageDirection,sourceFile,latestVersion,analysisStatistics,status"
                });
              } else {
                // source file
                return trados.apiClient.sourceFileApi().getSourceFile({
                  projectId: taskProject.id,
                  sourceFileId: f.id,
                  ...trados.getRegistrationResult(),
                  fields: "id,name,role,language,versions,targetLanguages,path"
                });
              }
            })
          )
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.taskInbox,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(files)
              );
            })
            .catch(e => {
              console.error(
                `[UI Extensibility] [extension] At least one API call failed`,
                e
              );
            });
        } else {
          // notification
          trados.showNotification(
            "No files selected in task files tab",
            trados.contexts.taskInbox,
            trados.notificationTypes.warning
          );
        }
        break;
      case "inboxActiveTab":
        const inboxActiveTab = detail.inboxActiveTab;
        // notification only
        trados.showNotification(
          `The active tab in the inbox view is <b>${inboxActiveTab}</b>`,
          trados.contexts.taskInbox,
          trados.notificationTypes.info
        );
        break;
      case "taskActiveTab":
        const taskActiveTab = detail.taskActiveTab;
        // notification only
        trados.showNotification(
          `The active tab in the task details view is <b>${taskActiveTab}</b>`,
          trados.contexts.taskInbox,
          trados.notificationTypes.info
        );
        break;
    }
  }
};

export const taskToolbarButtonRendered = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  const button = document.getElementById(detail.domElementId);
  const task = detail.task;
  if (button) {
    console.log("RENDERED TASK TOOLBAR BUTTON", task);
  }
};

export const taskToolbarButtonClicked = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  if (detail.task) {
    const task = detail.task;
    console.log("CLICKED TASK TOOLBAR BUTTON", task);
  }
};
