import { trados } from "@sdl/extensibility"; // doesn't work until new extensibility public package published
//import { trados } from "../../../../../../../public-packages/extensibility/src/index"; // works
import type {
  Project,
  TargetFile
} from "@sdl/extensibility/lib/lc-public-api/models";
//import type {
//  Project,
//  TargetFile
//} from "../../../../../../../public-packages/extensibility/dist/lib/lc-public-api/models/index";
import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { ProjectImportance } from "../types";
import {
  download,
  downloadData,
  getEnvironmentUrlPartByHost,
  logExtensionData,
  updateProjectImportanceList
} from "./helpers";

export let currentProject: Project | undefined = undefined;
export let projectImportanceList: ProjectImportance[] = [];

export const compareProjectApiToLocalDataButtonClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const clickedSelector = detail.value; // extensibilityComponents sets value in menu item handler, so selector string

  if (clickedSelector) {
    switch (clickedSelector) {
      case "selectedProjects":
        const selectedProjects = detail.selectedProjects;
        if (selectedProjects?.length) {
          // notification
          trados.showNotification(
            `Getting data via Public API for ${selectedProjects.length} selected project(s)`,
            trados.contexts.projects,
            trados.notificationTypes.info
          );

          // project api call for every selected project
          Promise.all(
            selectedProjects.map(p =>
              trados.apiClient.projectApi().getProject({
                projectId: p.id,
                ...trados.getRegistrationResult(),
                fields:
                  "name,description,dueBy,createdAt,status,statusHistory,languageDirections,customer,createdBy,location,projectTemplate,translationEngine,fileProcessingConfiguration,pricingModel,workflow,projectPlan,analytics,analysisStatistics,quote,customFields,tqaProfile,forceOnline,quoteTemplate,projectGroup,projectManagers"
              })
            )
          )
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.projects,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(selectedProjects)
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
            "No projects selected in projects list",
            trados.contexts.projects,
            trados.notificationTypes.warning
          );
        }
        break;
      case "project":
        const project = detail.project;
        if (project) {
          // notification
          trados.showNotification(
            "Getting data via Public API",
            trados.contexts.projects,
            trados.notificationTypes.info
          );

          // project details api call
          trados.apiClient
            .projectApi()
            .getProject({
              projectId: project.id,
              ...trados.getRegistrationResult(),
              fields:
                "name,description,dueBy,createdAt,status,statusHistory,languageDirections,customer,createdBy,location,projectTemplate,translationEngine,fileProcessingConfiguration,pricingModel,workflow,projectPlan,analytics,analysisStatistics,quote,customFields,tqaProfile,forceOnline,quoteTemplate,projectGroup,projectManagers"
            })
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.projects,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(project)
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
      case "selectedFile":
        const fileProject = detail.project;
        const file = detail.selectedFile; // if file has status property, call targetFileApi instead of sourceFileApi
        if (fileProject && file) {
          // notification
          trados.showNotification(
            "Getting data via Public API",
            trados.contexts.projects,
            trados.notificationTypes.info
          );

          // file api call
          const fileIsTargetFile = Boolean((file as TargetFile).status);

          if (fileIsTargetFile) {
            // target file
            trados.apiClient
              .targetFileApi()
              .getTargetFile({
                projectId: fileProject.id,
                targetFileId: file.id,
                ...trados.getRegistrationResult(),
                fields:
                  "id,name,languageDirection,sourceFile,latestVersion,analysisStatistics,status"
              })
              .then(apiData => {
                // notification
                trados.showNotification(
                  "Downloading files for comparison",
                  trados.contexts.projects,
                  trados.notificationTypes.info
                );

                // download
                download(
                  `compare-api-data-${clickedSelector}.json`,
                  JSON.stringify(apiData)
                );
                download(
                  `compare-ui-data-${clickedSelector}.json`,
                  JSON.stringify(file)
                );
              })
              .catch(e => {
                console.error(
                  `[UI Extensibility] [extension] API call failed`,
                  e
                );
              });
          } else {
            // source file
            trados.apiClient
              .sourceFileApi()
              .getSourceFile({
                projectId: fileProject.id,
                sourceFileId: file.id,
                ...trados.getRegistrationResult(),
                fields: "id,name,role,language,versions,targetLanguages,path"
              })
              .then(apiData => {
                // notification
                trados.showNotification(
                  "Downloading files for comparison",
                  trados.contexts.projects,
                  trados.notificationTypes.info
                );

                // download
                download(
                  `compare-api-data-${clickedSelector}.json`,
                  JSON.stringify(apiData)
                );
                download(
                  `compare-ui-data-${clickedSelector}.json`,
                  JSON.stringify(file)
                );
              })
              .catch(e => {
                console.error(
                  `[UI Extensibility] [extension] API call failed`,
                  e
                );
              });
          }
        } else {
          // notification
          trados.showNotification(
            "No file selected in project files tab",
            trados.contexts.projects,
            trados.notificationTypes.warning
          );
        }
        break;
      case "selectedFiles":
        const files = detail.selectedFiles; // if file has status property, call targetFileApi instead of sourceFileApi
        const filesProject = detail.project;
        if (filesProject && files?.length) {
          // notification
          trados.showNotification(
            "Getting data via Public API",
            trados.contexts.projects,
            trados.notificationTypes.info
          );

          // file api call for every selected file
          Promise.all(
            files.map(f => {
              const fileIsTargetFile = Boolean((f as TargetFile).status);
              if (fileIsTargetFile) {
                // target file
                return trados.apiClient.targetFileApi().getTargetFile({
                  projectId: filesProject.id,
                  targetFileId: f.id,
                  ...trados.getRegistrationResult(),
                  fields:
                    "id,name,languageDirection,sourceFile,latestVersion,analysisStatistics,status"
                });
              } else {
                // source file
                return trados.apiClient.sourceFileApi().getSourceFile({
                  projectId: filesProject.id,
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
                trados.contexts.projects,
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
            "No files selected in project files tab",
            trados.contexts.projects,
            trados.notificationTypes.warning
          );
        }
        break;
      case "selectedTaskHistoryTask":
        const taskHistoryTask = detail.selectedTaskHistoryTask;
        if (taskHistoryTask) {
          // notification
          trados.showNotification(
            "Getting data via Public API",
            trados.contexts.projects,
            trados.notificationTypes.info
          );

          // task api call
          trados.apiClient
            .taskApi()
            .getTask({
              taskId: taskHistoryTask.id,
              ...trados.getRegistrationResult(),
              fields:
                "id,status,taskType,input,inputFiles,owner,assignees,dueBy,createdAt,outcome,comment,project,failedTask,completedAt"
            })
            .then(apiData => {
              // notification
              trados.showNotification(
                "Downloading files for comparison",
                trados.contexts.projects,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(taskHistoryTask)
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
            "No task selected in project task history tab",
            trados.contexts.projects,
            trados.notificationTypes.warning
          );
        }
        break;
      case "selectedStagesTasks":
        const stagesTasks = detail.selectedStagesTasks;
        if (stagesTasks?.length) {
          // notification
          trados.showNotification(
            `Getting data via Public API for ${stagesTasks.length} selected stages task(s)`,
            trados.contexts.projects,
            trados.notificationTypes.info
          );

          // task api call for every selected task in stages tab
          Promise.all(
            stagesTasks.map(t =>
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
                "Getting data via Public API",
                trados.contexts.projects,
                trados.notificationTypes.info
              );

              // download
              download(
                `compare-api-data-${clickedSelector}.json`,
                JSON.stringify(apiData)
              );
              download(
                `compare-ui-data-${clickedSelector}.json`,
                JSON.stringify(stagesTasks)
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
            "No tasks selected in project stages tab",
            trados.contexts.projects,
            trados.notificationTypes.warning
          );
        }
        break;
      case "projectActiveTab":
        const activeTab = detail.projectActiveTab;
        // notification only
        trados.showNotification(
          `The active tab in the project details view is <b>${activeTab}</b>`,
          trados.contexts.projects,
          trados.notificationTypes.info
        );
        break;
    }
  }
};

export const invoiceButtonRendered = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  // update button visible state
  const hidden = Boolean(detail.project && !detail.project.quote);
  trados.updateElement("invoiceButton", {
    hidden: hidden
  });
  trados.updateElement("invoiceButtonGeneratedApi", {
    hidden: hidden
  });
};

export const invoiceButtonClicked = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);

  // notifications
  trados.showNotification(
    "Requesting LC UI Extensibility service to perform file download API call (LC Public API quote-report)",
    trados.contexts.projects,
    trados.notificationTypes.success
  );

  // api calls - public api (pdf quote download)
  trados
    .callApi({
      url: `public-api/v1/projects/${detail.project!.id}/quote-report`, // LC Public API
      method: "GET",
      fileName: "invoice.pdf" // when fileName exists, FileDownloader will be used in extensibility service to make the LC Public API call
    })
    .then(_data => {
      trados.showNotification(
        "Get project quote public API call completed successfully. Download will begin shortly.",
        trados.contexts.projects,
        trados.notificationTypes.success
      );
    })
    .catch(reason => {
      console.error("Get project quote call failed", reason);
      trados.showNotification(
        "Get project quote public API call failed.",
        trados.contexts.projects,
        trados.notificationTypes.fail
      );
    });

  // api calls - add-on backend api (testfile download)
  // todo: uncomment and check when add-on management add-on back-end proxy endpoint will support file download. currently only json responses are supported.
  /*
    const testFileAddOnApiCallRequestEvent = new CustomEvent(
      EventType.CallAddonApi,
      {
        detail: {
          config: {
            extensionId: extensionId,
            context: "projects",
            url: "api/files/test-file/testfile.txt",
            method: "GET",
            fileName: "testfile.txt",
            callId: "getTestFileAddonApiCall"
          }
        }
      }
    );
    window.dispatchEvent(testFileAddOnApiCallRequestEvent);

    // response handler
    // todo: uncomment and check when add-on management add-on back-end proxy endpoint will support file download. currently only json responses are supported.
    /*
    if (apiCallCompleteDetail.callId === "getTestFileAddonApiCall") {
      publish("showNotification", {
          context: "projects",
          type: apiCallCompleteDetail.success
            ? "success"
            : "fail",
          text: apiCallCompleteDetail.success
            ? "Test file download from add-on API call completed successfully. Download will begin shortly."
            : "Test file download from add-on API call failed."
      });
    }
    */
};

// duplicated create invoice functionality to exemplify how it works with the
// generated TypeScript client alternative
export const invoiceButtonGeneratedApiClicked = async (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  const projectId = detail.project!.id;

  // notifications
  trados.showNotification(
    "Requesting the export of the quote report (LC Public API ExportQuoteReport)",
    trados.contexts.projects
  );

  /* remove try catch when https://jira.sdl.com/browse/LTLC-92923 fixed
  const exportQuoteReport = await trados.apiClient.quoteApi.exportQuoteReport({
    projectId,
    ...trados.getRegistrationResult()
  });
  */
  try {
    await trados.apiClient.quoteApi().exportQuoteReport({
      projectId,
      ...trados.getRegistrationResult()
    });
  } catch (error) {
    // empty response instead of empty JSON response - https://jira.sdl.com/browse/LTLC-92923
  }

  //if (exportQuoteReport.id) {
  let exportStatusChecks = 0;
  let pollIntervalId: any = setInterval(async () => {
    await trados.apiClient
      .quoteApi()
      .pollQuoteReportExport({
        projectId,
        ...trados.getRegistrationResult()
      })
      .then(pollResponse => {
        exportStatusChecks++;
        console.log("----pollResponse", pollResponse);
        if (pollResponse.status === "completed") {
          clearInterval(pollIntervalId);
          pollIntervalId = null;
          trados.apiClient
            .quoteApi()
            .downloadQuoteReport({
              projectId,
              ...trados.getRegistrationResult()
            })
            .then(blob => {
              trados.showNotification(
                "Download quote report completed successfully.<br/>Preparing file. Download will begin shortly.",
                trados.contexts.projects,
                trados.notificationTypes.success
              );

              // for generated Public API, handle file download ourselves
              download("quoteReport.pdf", blob);
            })
            .catch(reason => {
              console.log("Failed to download quote report.", reason);
              trados.showNotification(
                "Failed to download the quote report.",
                trados.contexts.projects,
                trados.notificationTypes.fail
              );
            });
        } else {
          if (exportStatusChecks === 9) {
            clearInterval(pollIntervalId);
            pollIntervalId = null;
            trados.showNotification(
              "The export status was verified 10 times and it's not yet complete. We've given up checking.",
              trados.contexts.projects,
              trados.notificationTypes.fail
            );
          }
        }
      })
      .catch(reason => {
        console.log("Failed to poll quote report export status.", reason);
        trados.showNotification(
          "Failed to poll quote report export status.",
          trados.contexts.projects,
          trados.notificationTypes.fail
        );
      });
  }, 2000); // todo: 20000ms recommended interval, but isn't that too long a wait?
  //}
};

// open in new tab
export const openNewTabButtonClicked = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  const url = "https://www.rws.com";
  const windowName = "newTabFromLcUiExtension";
  window.open(url, windowName);
};

// navigate to project's template - set location and load new page
export const navigateButtonLoadRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  // update button visible state
  const hidden = Boolean(detail.project && !detail.project.id);
  trados.updateElement("navigateButtonLoad", {
    hidden: hidden
  });
};

// navigate to project's template - set location and load new page
export const navigateButtonLoadClicked = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  if (detail.project?.projectTemplate) {
    // either use project set in the render handler
    // or use custom event's detail: e.detail which is requested via the action's payload in activated-extensions json
    const projectTemplateId = detail.project.projectTemplate.id; // can be null; button is hidden if no project template id
    const destinationPath = `resources/project-templates/${projectTemplateId}`;

    // navigate to LC page event
    trados.navigate(destinationPath, trados.navigationTypes.load);
  }
};

// navigate to project's template - rely on router controller, no page load
export const navigateButtonRouteRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  // update button visible state
  const hidden = Boolean(detail.project && !detail.project.id);
  trados.updateElement("navigateButtonRoute", {
    hidden: hidden
  });
};

// navigate to project's template - rely on router controller, no page load
export const navigateButtonRouteClicked = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);
  if (detail.project?.projectTemplate) {
    // either use project set in the render handler
    // or use custom event's detail: e.detail which is requested via the action's payload in activated-extensions json
    const projectTemplateId = detail.project.projectTemplate.id; // can be null; button is hidden if no project template id
    const newPage = `resources/project-templates/${projectTemplateId}`;

    // navigate inside LC event
    trados.navigate(newPage, trados.navigationTypes.route);
  }
};

// get local data
export const getLocalDataButtonClicked = (detail: ExtensibilityEventDetail) => {
  logExtensionData(detail);
  if (detail.project) {
    trados
      .getLocalData(trados.contexts.projects)
      .then(downloadData)
      .catch(reason => {
        console.error("Failed to get local data", reason);
        trados.showNotification(
          "Failed to get local data.",
          trados.contexts.projects,
          trados.notificationTypes.fail
        );
      });
  }
};

// button rendered - setProjectImportanceButton
export const setProjectImportanceButtonRendered = (
    detail: ExtensibilityEventDetail
) => {
    logExtensionData(detail);
    currentProject = detail.project; // used in index.ts apiCallComplete handler

    // should get project importance call be made now?!?
    if (
        detail.project &&
        !projectImportanceList.find(i => i.projectId === detail.project!.id)
    ) {
        console.log(
            "[UI Extensibility] [extension] Get project importance from add-on API"
        );
        // call add-on API to get project importance
        trados
            .callAddonApi({
                url: `api/project-metadata/project-id/${detail.project.id}`,
                method: "GET"
            })
            .then(data => {
                if (data && data.responseData) {
                    if (data.responseData.errorCode) {
                        console.warn(
                            "Failed to get project metadata (importance).",
                            data.responseData.errorCode,
                            data.responseData.message
                        );
                    } else {
                        if (typeof data.responseData !== "string") {
                            setGetProjectImportanceApiResponseHandler(data, false);
                        }
                    }
                }
            })
            .catch(reason => {
                console.error("Failed to get project metadata (importance)", reason);
                trados.showNotification(
                    "Failed to get local data.",
                    trados.contexts.projects,
                    trados.notificationTypes.fail
                );
            });

        // add to projectImportanceList as pending
        projectImportanceList.push({
            projectId: detail.project.id!,
            pending: true
        });
    }
};

// call add-on API - POST/PUT/DELETE
export const setProjectImportanceButtonClicked = (
    detail: ExtensibilityEventDetail
) => {
    logExtensionData(detail);
    if (detail.project) {
        const importanceToSet = detail.value; // extensibilityComponents sets value in menu item handler, so high/medium/low
        if (importanceToSet) {
            const currentProjectImportance = projectImportanceList.find(
                (i: ProjectImportance) =>
                    i.projectId === detail.project!.id && !i.pending
            );
            // update button disabled state
            trados.updateElement("setProjectImportanceButton", {
                disabled: true
            });

            const deleteProjectImportance = importanceToSet === "none";
            if (deleteProjectImportance && currentProjectImportance) {
                trados
                    .callAddonApi({
                        url: `api/project-metadata/${currentProjectImportance.id}`,
                        method: "DELETE"
                    })
                    .then(data => {
                        if (data.responseData && currentProject) {
                            // remove from local projectImportanceList
                            updateProjectImportanceList(data);

                            // update button text
                            trados.updateElement("setProjectImportanceButton", {
                                icon: "x-fal fa-exclamation-circle",
                                text: "Set Importance",
                                disabled: false,
                                menuItems: [
                                    {
                                        index: 4, // unset importance
                                        disabled: true
                                    }
                                ]
                            });
                        }
                    })
                    .catch(reason => {
                        console.error(
                            "Failed to delete project metadata (importance)",
                            reason
                        );
                        trados.showNotification(
                            "Failed to delete project importance.",
                            trados.contexts.projects,
                            trados.notificationTypes.fail
                        );
                    });
            } else {
                trados
                    .callAddonApi({
                        url: `api/project-metadata${currentProjectImportance ? "/" + currentProjectImportance.id : ""
                            }`,
                        method: currentProjectImportance ? "PUT" : "POST",
                        body: JSON.stringify({
                            projectId: detail.project.id,
                            importance: importanceToSet
                        })
                    })
                    .then(data => {
                        setGetProjectImportanceApiResponseHandler(data, true);
                    })
                    .catch(reason => {
                        console.error(
                            "Failed to set project metadata (importance)",
                            reason
                        );
                        trados.showNotification(
                            "Failed to set project importance.",
                            trados.contexts.projects,
                            trados.notificationTypes.fail
                        );
                    });
            }
        } else {
            // main button was clicked, not High/Medium/Low menu items, so do nothing
        }
    }
};

export const setGetProjectImportanceApiResponseHandler = (
    data: any,
    isSetResponsone: boolean
) => {
    if (data.responseData && currentProject) {
        // update/add to local projectImportanceList
        updateProjectImportanceList(data);
        const respImportance = data.responseData.importance;

        // update button text
        trados.updateElement("setProjectImportanceButton", {
            icon:
                respImportance.toLowerCase() === "high"
                    ? "x-fal fa-chevron-circle-up"
                    : respImportance.toLowerCase() === "medium"
                        ? "x-fal fa-dot-circle"
                        : respImportance.toLowerCase() === "low"
                            ? "x-fal fa-chevron-circle-down"
                            : "x-fal fa-exclamation-circle",
            text: respImportance,
            disabled: isSetResponsone ? false : undefined,
            menuItems: [
                {
                    index: 4, // unset importance
                    disabled: false
                }
            ]
        });
    }
};