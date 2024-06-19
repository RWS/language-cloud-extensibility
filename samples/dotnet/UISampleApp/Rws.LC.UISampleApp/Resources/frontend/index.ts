/*************************************************
 * this file is based on C:\SDL-Products\nimbus-ui\packages\projects\src\extensibility\PoCExtension.ts
 * but wrapped into a self-invoked function
 *
 * the config is mocked in activated-extensions.json
 * the extension script is mocked in the current file: run "tsc" from C:\SDL-Products\nimbus-ui\packages\projects-app\src\mocks\ui-extensibility to update the PoCExtension.js file
 * the extension script is a self invoked function in this file that should be served by a separate website to simulate a third party resource: see https://confluence.sdl.com/display/LPD/UI+PoC+Add-on+-+Local+Setup for details
 *************************************************/
import { trados } from "@sdl/extensibility";
import {
    ExtensionElement,
    ExtensibilityEventDetail,
    ElementType
} from "@sdl/extensibility-types/extensibility";

// projects handlers
import {
    backToDefaultFontSizeButtonClicked,
    dashboardMainPanelRendered,
    dashboardSidebarBoxRendered,
    getLocalDashboardDataButtonClicked,
    increaseFontSizeButtonClicked
} from "./handlers/projectDashboardHandlers";
import {
    getLocalFilesDataButtonClicked,
    hideNotTranslatedFilesButtonClicked,
    showAllFilesButtonClicked
} from "./handlers/projectFilesHandlers";
import {
    getLocalSelectedProjectsDataButtonRendered,
    getLocalSelectedProjectsDataButtonClicked
} from "./handlers/projectsListToolbarHandlers";
import {
    getLocalStagesDataButtonClicked,
    highlightPopulatedStagesButtonClicked,
    removeStagesHighlightsButtonClicked
} from "./handlers/projectStagesHadlers";
import { extensionsHelperTabRendered } from "./handlers/projectTabbarHandlers";
import {
    getLocalTaskHistoryDataButtonClicked,
    hideFailedTasksButtonClicked,
    highlightSelectedTaskButtonClicked,
    highlightSelectedTaskButtonRendered,
    showAllTasksButtonClicked,
    taskHistorySidebarBoxRendered
} from "./handlers/projectTaskHistoryHandlers";
import {
    compareProjectApiToLocalDataButtonClicked,
    getLocalDataButtonClicked,
    invoiceButtonClicked,
    invoiceButtonGeneratedApiClicked,
    invoiceButtonRendered,
    navigateButtonLoadClicked,
    navigateButtonLoadRendered,
    navigateButtonRouteClicked,
    navigateButtonRouteRendered,
    openNewTabButtonClicked,
    setProjectImportanceButtonClicked,
    setProjectImportanceButtonRendered
} from "./handlers/projectToolbarHandlers";

// task-inbox handlers
import {
    activeTasksListToolbarButtonClicked,
    activeTasksListToolbarButtonRendered,
    completedTasksListToolbarButtonClicked,
    completedTasksListToolbarButtonRendered,
    newTasksListToolbarButtonClicked,
    newTasksListToolbarButtonRendered
} from "./handlers/tasksListToolbarHandlers";
import { taskSidebarBoxRendered } from "./handlers/tasksListPreviewHandlers";
import {
    compareTaskApiToLocalDataButtonClicked,
    taskToolbarButtonClicked,
    taskToolbarButtonRendered
} from "./handlers/taskToolbarHandlers";
import { taskTabRendered } from "./handlers/taskTabbarHandlers";
import {
    detailsMainBoxRendered,
    taskDetailsToolbarButtonClicked,
    taskDetailsToolbarButtonRendered,
    taskFilesToolbarButtonClicked,
    taskFilesToolbarButtonRendered,
    taskDetailsSidebarBoxRendered
} from "./handlers/taskDetailsHandlers";


// extension elements: the JSON describing custom elements that will be added in the UI
const elements: ExtensionElement[] = [
    // projects elements
    /*
    {
        elementId: "compareApiToLocalDataButton",
        icon: "x-fal fa-copy",
        text: "API vs Local data",
        location: "project-details-toolbar",
        type: "button",
        hidden: false,
        menu: [
            {
                text: "selectedProjects",
                value: "selectedProjects",
                icon: "x-fal fa-copy"
            },
            {
                text: "project",
                value: "project",
                icon: "x-fal fa-copy"
            },
            {
                text: "selectedFile",
                value: "selectedFile",
                icon: "x-fal fa-copy"
            },
            {
                text: "selectedFiles",
                value: "selectedFiles",
                icon: "x-fal fa-copy"
            },
            {
                text: "selectedTaskHistoryTask",
                value: "selectedTaskHistoryTask",
                icon: "x-fal fa-copy"
            },
            {
                text: "selectedStagesTasks",
                value: "selectedStagesTasks",
                icon: "x-fal fa-copy"
            },
            { separator: true },
            {
                text: "projectActiveTab (notification only)",
                value: "projectActiveTab",
                icon: "x-fal fa-exclamation-circle"
            }
        ],
        actions: [
            {
                eventType: "onclick",
                eventHandler: compareProjectApiToLocalDataButtonClicked,
                payload: [
                    "selectedProjects",
                    "project",
                    "selectedFile",
                    "selectedFiles",
                    "selectedTaskHistoryTask",
                    "selectedStagesTasks",
                    "projectActiveTab"
                ]
            }
        ]
    },*/
    {
        elementId: "invoiceButton",
        icon: "x-fal fa-newspaper",
        text: "Create Invoice",
        location: "project-details-toolbar",
        type: "button",
        hidden: true,
        actions: [
            {
                eventType: "onrender",
                eventHandler: invoiceButtonRendered,
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: invoiceButtonClicked,
                payload: ["project"]
            }
        ]
    },
    {
        elementId: "invoiceButtonGeneratedApi",
        icon: "x-fal fa-newspaper",
        text: "Create Invoice (generated API)",
        location: "project-details-toolbar",
        type: "button",
        hidden: true,
        actions: [
            {
                eventType: "onrender",
                eventHandler: invoiceButtonRendered,
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: invoiceButtonGeneratedApiClicked,
                payload: ["project"]
            }
        ]
    },
    /*
    {
        elementId: "openNewTabButton",
        icon: "x-fal fa-external-link",
        iconAlign: "right",
        text: "Open rws.com in New tab",
        location: "project-details-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: openNewTabButtonClicked,
                payload: ["project"]
            }
        ]
    },*/
    {
        elementId: "openNewTabButtonLink",
        icon: "x-fal fa-external-link",
        text: "Open rws.com in New tab",
        location: "project-details-toolbar",
        type: "button",
        isLink: true,
        href: "https://www.rws.com"
    },
    /*
    {
        elementId: "navigateButtonLoad",
        icon: "x-fal fa-clipboard",
        text: "View Project Template (page load)",
        location: "project-details-toolbar",
        type: "button",
        hidden: true,
        actions: [
            {
                eventType: "onrender",
                eventHandler: navigateButtonLoadRendered,
                payload: ["project"]
            },
            {
                eventType: "onclick",
                eventHandler: navigateButtonLoadClicked,
                payload: ["project"]
            }
        ]
    },*/
    {
        elementId: "navigateButtonRoute",
        icon: "x-fal fa-clipboard",
        text: "View Project Template",  // (route)",
        location: "project-details-toolbar",
        type: "button",
        hidden: true,
        actions: [
            {
                eventType: "onrender",
                eventHandler: navigateButtonRouteRendered,
                payload: ["project"]
            },
            {
                eventType: "onclick",
                eventHandler: navigateButtonRouteClicked,
                payload: ["project"]
            }
        ]
    },
    {
        elementId: "getLocalDataButton",
        icon: "x-fal fa-table",
        text: "Get Local Data",
        location: "project-details-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: getLocalDataButtonClicked,
                //payload: ["project"]
                payload: [
                    "selectedProjects",
                    "project",
                    "selectedFile",
                    "selectedFiles",
                    "selectedTaskHistoryTask",
                    "selectedStagesTasks",
                    "projectActiveTab"
                ]
            }
        ]
    },
    {
        elementId: "setProjectImportanceButton",
        icon: "x-fal fa-exclamation-circle",
        text: "Set Importance",
        location: "project-details-toolbar",
        type: "button",
        menu: [
            {
                text: "High",
                value: "high",
                icon: "x-fal fa-chevron-circle-up"
            },
            {
                text: "Medium",
                value: "medium",
                icon: "x-fal fa-dot-circle"
            },
            {
                text: "Low",
                value: "low",
                icon: "x-fal fa-chevron-circle-down"
            },
            { separator: true },
            {
                text: "Unset importance",
                value: "none",
                icon: "x-fal fa-trash",
                disabled: true
            }
        ],
        actions: [
            {
                eventType: "onrender",
                eventHandler: setProjectImportanceButtonRendered,
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: setProjectImportanceButtonClicked,
                payload: ["project"]
            }
        ]
    },
    {
        elementId: "extensionsHelper",
        text: "Extensions Helper",
        location: "project-details-tabpanel",
        type: "tab",
        actions: [
            {
                eventType: "onrender",
                eventHandler: extensionsHelperTabRendered,
                payload: ["project", "selectedProjects"]
            }
        ]
    },
    {
        elementId: "dashboardMain1Box",
        text: "Custom Panel 1",
        location: "project-details-dashboard-main",
        type: "panel",
        actions: [
            {
                eventType: "onrender",
                eventHandler: (detail: ExtensibilityEventDetail) => {
                    dashboardMainPanelRendered(detail, 1);
                },
                payload: []
            }
        ]
    },
    {
        elementId: "dashboardMain2Box",
        text: "Custom Panel 2",
        location: "project-details-dashboard-main",
        type: "panel",
        actions: [
            {
                eventType: "onrender",
                eventHandler: (detail: ExtensibilityEventDetail) => {
                    dashboardMainPanelRendered(detail, 2);
                },
                payload: []
            }
        ]
    },
    {
        elementId: "dashboardMain3Box",
        text: "Custom Panel 3",
        location: "project-details-dashboard-main",
        type: "panel",
        actions: [
            {
                eventType: "onrender",
                eventHandler: (detail: ExtensibilityEventDetail) => {
                    dashboardMainPanelRendered(detail, 3);
                },
                payload: []
            }
        ]
    },
    {
        elementId: "dashboardSidebarBox",
        text: "Sidebar Box",
        location: "project-details-dashboard-sidebar",
        type: "sidebarBox",
        actions: [
            {
                eventType: "onrender",
                eventHandler: dashboardSidebarBoxRendered,
                payload: []
            }
        ]
    },
    {
        elementId: "getLocalDashboardDataButton",
        icon: "x-fal fa-table",
        iconAlign: "right",
        text: "Get Local Data",
        location: "project-details-dashboard-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: getLocalDashboardDataButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "increaseFontSizeButton",
        icon: "x-fal fa-font",
        iconAlign: "right",
        text: "Increase font size",
        location: "project-details-dashboard-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: increaseFontSizeButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "backToDefaultFontSizeButton",
        icon: "x-fal fa-arrow-down",
        iconAlign: "right",
        text: "Back to default font size",
        location: "project-details-dashboard-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: backToDefaultFontSizeButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "getLocalStagesDataButton",
        icon: "x-fal fa-calendar",
        iconAlign: "right",
        text: "Get local data",
        location: "project-details-stages-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: getLocalStagesDataButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "highlightPopulatedStagesButton",
        icon: "x-fal fa-folder-open",
        iconAlign: "right",
        text: "Highlight populated stages",
        location: "project-details-stages-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: highlightPopulatedStagesButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "removeStagesHighlightsButton",
        icon: "x-fal fa-folder",
        iconAlign: "right",
        text: "Remove stages highlights",
        location: "project-details-stages-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: removeStagesHighlightsButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "getLocalFilesDataButton",
        icon: "x-fal fa-calendar",
        iconAlign: "right",
        text: "Get local data",
        location: "project-details-files-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: getLocalFilesDataButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "hideNotTranslatedFilesButton",
        icon: "x-fal fa-ban",
        iconAlign: "right",
        text: "Hide not translated files",
        location: "project-details-files-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: hideNotTranslatedFilesButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "showAllFilesButton",
        icon: "x-fal fa-bars",
        iconAlign: "right",
        text: "Show all files",
        location: "project-details-files-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: showAllFilesButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "getLocalTaskHistoryDataButton",
        icon: "x-fal fa-calendar",
        iconAlign: "right",
        text: "Get local data",
        location: "project-details-task-history-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: getLocalTaskHistoryDataButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "hideFailedTasksButton",
        icon: "x-fal fa-ban",
        iconAlign: "right",
        text: "Hide Failed tasks",
        location: "project-details-task-history-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: hideFailedTasksButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "showAllButton",
        icon: "x-fal fa-bars",
        iconAlign: "right",
        text: "Show all tasks",
        location: "project-details-task-history-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: showAllTasksButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "taskHistorySidebarBox",
        text: "Sidebar Box",
        location: "project-details-task-history-sidebar",
        type: "sidebarBox",
        actions: [
            {
                eventType: "onrender",
                eventHandler: taskHistorySidebarBoxRendered,
                payload: ["selectedTaskHistoryTask"]
            }
        ]
    },
    {
        elementId: "highlightSelectedTaskButton",
        icon: "x-fal fa-folder-open",
        iconAlign: "right",
        text: "Highlight selected tasks",
        location: "project-details-task-history-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onrender",
                eventHandler: highlightSelectedTaskButtonRendered,
                payload: ["selectedTaskHistoryTask"]
            },
            {
                eventType: "onclick",
                eventHandler: highlightSelectedTaskButtonClicked,
                payload: []
            }
        ]
    },
    {
        elementId: "getLocalSelectedProjectsDataButton",
        icon: "x-fal fa-table",
        iconAlign: "right",
        text: "Get selection data",
        location: "projects-list-toolbar",
        type: "button",
        disabled: true,
        actions: [
            {
                eventType: "onrender",
                eventHandler: getLocalSelectedProjectsDataButtonRendered,
                payload: ["selectedProjects"]
            },
            {
                eventType: "onclick",
                eventHandler: getLocalSelectedProjectsDataButtonClicked,
                payload: ["selectedProjects"]
            }
        ]
    },

    // task-inbox elements
    /*
    {
        elementId: "compareApiToLocalDataButton",
        icon: "x-fal fa-copy",
        text: "API vs Local data",
        location: "task-toolbar",
        type: "button",
        hidden: false,
        menu: [
            {
                text: "selectedNewTasks",
                value: "selectedNewTasks",
                icon: "x-fal fa-copy"
            },
            {
                text: "selectedActiveTasks",
                value: "selectedActiveTasks",
                icon: "x-fal fa-copy"
            },
            {
                text: "selectedCompletedTasks",
                value: "selectedCompletedTasks",
                icon: "x-fal fa-copy"
            },
            {
                text: "newTaskPreview",
                value: "newTaskPreview",
                icon: "x-fal fa-copy"
            },
            {
                text: "activeTaskPreview",
                value: "activeTaskPreview",
                icon: "x-fal fa-copy"
            },
            {
                text: "completedTaskPreview",
                value: "completedTaskPreview",
                icon: "x-fal fa-copy"
            },
            {
                text: "task",
                value: "task",
                icon: "x-fal fa-copy"
            },
            {
                text: "selectedFiles",
                value: "selectedFiles",
                icon: "x-fal fa-copy"
            },
            { separator: true },
            {
                text: "inboxActiveTab (notification only)",
                value: "inboxActiveTab",
                icon: "x-fal fa-exclamation-circle"
            },
            {
                text: "taskActiveTab (notification only)",
                value: "taskActiveTab",
                icon: "x-fal fa-exclamation-circle"
            }
        ],
        actions: [
            {
                eventType: "onclick",
                eventHandler: compareTaskApiToLocalDataButtonClicked,
                payload: [
                    "selectedNewTasks",
                    "selectedActiveTasks",
                    "selectedCompletedTasks",
                    "newTaskPreview",
                    "activeTaskPreview",
                    "completedTaskPreview",
                    "inboxActiveTab",
                    "task",
                    "selectedFiles",
                    "taskActiveTab"
                ]
            }
        ]
    },*/
    {
        elementId: "custom-btn-new-tasks-list-toolbar",
        icon: "x-fal fa-newspaper",
        text: "New custom button",
        location: "new-tasks-list-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onrender",
                eventHandler: newTasksListToolbarButtonRendered,
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: newTasksListToolbarButtonClicked,
                payload: ["selectedNewTasks"]
            }
        ]
    },
    {
        elementId: "custom-btn-active-tasks-list-toolbar",
        icon: "x-fal fa-newspaper",
        text: "Active custom button",
        location: "active-tasks-list-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onrender",
                eventHandler: activeTasksListToolbarButtonRendered,
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: activeTasksListToolbarButtonClicked,
                payload: ["selectedActiveTasks"]
            }
        ]
    },
    {
        elementId: "custom-btn-completed-tasks-list-toolbar",
        icon: "x-fal fa-newspaper",
        text: "Completed custom button",
        location: "completed-tasks-list-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onrender",
                eventHandler: completedTasksListToolbarButtonRendered,
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: completedTasksListToolbarButtonClicked,
                payload: ["selectedCompletedTasks"]
            }
        ]
    },
    {
        elementId: "newTaskSidebarBox",
        text: "New Sidebar Box",
        location: "new-tasks-list-sidebar",
        type: "sidebarBox",
        actions: [
            {
                eventType: "onrender",
                eventHandler: taskSidebarBoxRendered,
                payload: []
            }
        ]
    },
    {
        elementId: "activeTaskSidebarBox",
        text: "Active Sidebar Box",
        location: "active-tasks-list-sidebar",
        type: "sidebarBox",
        actions: [
            {
                eventType: "onrender",
                eventHandler: taskSidebarBoxRendered,
                payload: []
            }
        ]
    },
    {
        elementId: "completedTaskSidebarBox",
        text: "Completed Sidebar Box",
        location: "completed-tasks-list-sidebar",
        type: "sidebarBox",
        actions: [
            {
                eventType: "onrender",
                eventHandler: taskSidebarBoxRendered,
                payload: []
            }
        ]
    },
    {
        elementId: "custom-btn-task-toolbar",
        icon: "x-fal fa-newspaper",
        text: "Custom button",
        location: "task-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onrender",
                eventHandler: taskToolbarButtonRendered,
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: taskToolbarButtonClicked,
                payload: ["task", "taskActiveTab"]
            }
        ]
    },
    {
        elementId: "custom-tab",
        text: "Custom Tab",
        location: "task-tabpanel",
        type: "tab",
        actions: [
            {
                eventType: "onrender",
                eventHandler: taskTabRendered,
                payload: ["task", "taskActiveTab"]
            }
        ]
    },
    {
        elementId: "taskSidebarBox",
        text: "Task Details Sidebar Box",
        location: "task-sidebar",
        type: "sidebarBox",
        actions: [
            {
                eventType: "onrender",
                eventHandler: taskDetailsSidebarBoxRendered,
                payload: []
            }
        ]
    },
    {
        elementId: "custom-btn-task-details-toolbar",
        icon: "x-fal fa-newspaper",
        text: "Custom task details toolbar button",
        location: "task-details-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onrender",
                eventHandler: taskDetailsToolbarButtonRendered,
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: taskDetailsToolbarButtonClicked,
                payload: ["task", "taskActiveTab"]
            }
        ]
    },
    {
        elementId: "detailsMain1Box",
        text: "Custom Panel 1",
        location: "task-details-main",
        type: "panel",
        actions: [
            {
                eventType: "onrender",
                eventHandler: (detail: ExtensibilityEventDetail) => {
                    detailsMainBoxRendered(detail, 1);
                },
                payload: []
            }
        ]
    },
    {
        elementId: "detailsMain2Box",
        text: "Custom Panel 2",
        location: "task-details-main",
        type: "panel",
        actions: [
            {
                eventType: "onrender",
                eventHandler: (detail: ExtensibilityEventDetail) => {
                    detailsMainBoxRendered(detail, 2);
                },
                payload: []
            }
        ]
    },
    {
        elementId: "custom-btn-task-files-toolbar",
        icon: "x-fal fa-newspaper",
        text: "Custom button",
        location: "task-files-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onrender",
                eventHandler: taskFilesToolbarButtonRendered,
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: taskFilesToolbarButtonClicked,
                payload: ["task", "taskActiveTab", "selectedFiles"]
            }
        ]
    }
];

/*
// extra buttons for LTLC-85181
const allToolbarLocations = [
    // projects
    "projects-list-toolbar",
    "project-details-toolbar",
    "project-details-dashboard-toolbar",
    "project-details-stages-toolbar",
    "project-details-files-toolbar",
    "project-details-task-history-toolbar",
    // task-inbox
    "new-tasks-list-toolbar",
    "active-tasks-list-toolbar",
    "completed-tasks-list-toolbar",
    "task-toolbar",
    "task-details-toolbar",
    "task-files-toolbar"
];

allToolbarLocations.forEach(l =>
    elements.push(
        {
            elementId: `${l}-cstm-btn-actions`,
            icon: "x-fal fa-info-circle",
            text: "Change Target button",
            // @ts-ignore
            location: l,
            type: "button",
            menu: [
                // toggle hidden
                {
                    icon: "x-fal fa-angle-right",
                    text: "Hide",
                    value: "hide"
                },
                {
                    icon: "x-fal fa-angle-right",
                    text: "Show",
                    value: "show"
                },
                {
                    separator: true
                },
                // toggle disabled
                {
                    icon: "x-fal fa-angle-right",
                    text: "Disable",
                    value: "disable"
                },
                {
                    icon: "x-fal fa-angle-right",
                    text: "Enable",
                    value: "enable"
                },
                {
                    separator: true
                },
                // toggle text
                {
                    icon: "x-fal fa-angle-right",
                    text: "Make text uppercase",
                    value: "uppercase"
                },
                {
                    icon: "x-fal fa-angle-right",
                    text: "Make text casing default",
                    value: "defaultcase"
                },
                {
                    separator: true
                },
                // toggle icon
                {
                    icon: "x-fal fa-angle-right",
                    text: "Set star icon",
                    value: "staricon"
                },
                {
                    icon: "x-fal fa-angle-right",
                    text: "Set info icon",
                    value: "infoicon"
                },
                {
                    separator: true
                },
                // toggle menu
                {
                    icon: "x-fal fa-angle-right",
                    text: "Disable & make uppercase & set star icon for dropdown menu options",
                    value: "disablemenuoptions"
                },
                {
                    icon: "x-fal fa-angle-right",
                    text: "Enable & make default case & set info icon for dropdown menu options",
                    value: "enablemenuoptions"
                }
            ],
            actions: [
                {
                    eventType: "onclick",
                    eventHandler: (detail: ExtensibilityEventDetail) => {
                        switch (detail.value) {
                            case "hide":
                                trados.updateElement(`${l}-cstm-btn-target`, { hidden: true });
                                break;
                            case "show":
                                trados.updateElement(`${l}-cstm-btn-target`, { hidden: false });
                                break;
                            case "disable":
                                trados.updateElement(`${l}-cstm-btn-target`, {
                                    disabled: true
                                });
                                break;
                            case "enable":
                                trados.updateElement(`${l}-cstm-btn-target`, {
                                    disabled: false
                                });
                                break;
                            case "uppercase":
                                trados.updateElement(`${l}-cstm-btn-target`, {
                                    text: "TARGET"
                                });
                                break;
                            case "defaultcase":
                                trados.updateElement(`${l}-cstm-btn-target`, {
                                    text: "Target"
                                });
                                break;
                            case "staricon":
                                trados.updateElement(`${l}-cstm-btn-target`, {
                                    icon: "x-fal fa-star"
                                });
                                break;
                            case "infoicon":
                                trados.updateElement(`${l}-cstm-btn-target`, {
                                    icon: "x-fal fa-info"
                                });
                                break;
                            case "disablemenuoptions":
                                trados.updateElement(`${l}-cstm-btn-target`, {
                                    menuItems: [
                                        {
                                            index: 0,
                                            disabled: true,
                                            text: "MENU OPTION 1",
                                            icon: "x-fal fa-star"
                                        },
                                        {
                                            index: 1,
                                            disabled: true,
                                            text: "MENU OPTION 2",
                                            icon: "x-fal fa-star"
                                        }
                                    ]
                                });
                                break;
                            case "enablemenuoptions":
                                trados.updateElement(`${l}-cstm-btn-target`, {
                                    menuItems: [
                                        {
                                            index: 0,
                                            disabled: false,
                                            text: "Menu option 1",
                                            icon: "x-fal fa-angle-right"
                                        },
                                        {
                                            index: 1,
                                            disabled: false,
                                            text: "Menu option 2",
                                            icon: "x-fal fa-angle-right"
                                        }
                                    ]
                                });
                                break;
                        }
                    },
                    payload: []
                }
            ]
        },
        {
            elementId: `${l}-cstm-btn-target`,
            icon: "x-fal fa-dot-circle",
            text: "Target",
            location: l,
            type: "button",
            menu: [
                {
                    text: "Menu option 1",
                    icon: "x-fal fa-angle-right"
                },
                {
                    text: "Menu option 2",
                    icon: "x-fal fa-angle-right"
                }
            ]
        },
        {
            elementId: `${l}-cstm-btn1`,
            icon: "x-fal fa-info-circle",
            text: "`\"'~!@#$%^&*()_+-=:?/><;,.\\|ăîâșț",
            location: l,
            type: "button"
        },
        {
            elementId: `${l}-cstm-btn2`,
            icon: "x-fal fa-info-circle",
            text: "Long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long text",
            location: l,
            type: "button",
            menu: [
                {
                    text: "Long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long text"
                },
                {
                    text: "Some text"
                },
                {
                    separator: true
                },
                {
                    text: "`\"'~!@#$%^&*()_+-=:?/><;,.\\|ăîâșț"
                }
            ]
        },
        {
            elementId: `${l}-cstm-btn3`,
            icon: "x-fal fa-info-circle",
            text: "Long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long text",
            location: l,
            type: "button"
        },
        {
            elementId: `${l}-cstm-btn4`,
            icon: "x-fal fa-info-circle",
            text: "Long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long link",
            location: l,
            type: "button",
            isLink: true,
            href: "mailto:api.extensibility.team@rws.com"
        }
    )
);

// extra containers for LTLC-85185
const renderCounter: any = {};
const renderContent = (
    type: ElementType,
    detail: ExtensibilityEventDetail,
    elementId: string
) => {
    renderCounter[elementId]++;
    const container = document.getElementById(detail.domElementId);
    if (container) {
        // remove content for rerenders
        container.innerHTML = "";
        const elem = document.createElement("div");
        const imgSize = type === "sidebarBox" ? "500x500" : "2000x2000";
        const imgWidthHight = type === "sidebarBox" ? "500" : "2000";
        elem.innerHTML = `<p>Custom element rendered <b>${renderCounter[elementId]}</b> times.</p>`;
        elem.innerHTML += "`\"'~!@#$%^&*()_+-=:?/><;,.\\|ăîâșț";
        elem.innerHTML += `<p>LongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaksLongTextWithNoBreaks</p>`;
        elem.innerHTML += `<img src="https://via.placeholder.com/${imgSize}/008080/fff?text=placeholder+${imgSize}" style="width: ${imgWidthHight}px; height: ${imgWidthHight}px">`;

        if (type === "tab") {
            elem.className = "x-panel-header-title-light-framed";
            elem.setAttribute("style", "margin-bottom: 10px");
            container.style.position = "relative";
        }

        // add new content
        container.appendChild(elem);
    }
};

const allPanelLocations = [
    // projects
    "project-details-dashboard-main",
    // task-inbox
    "task-details-main"
];

allPanelLocations.forEach(l => {
    renderCounter[`${l}-cstm-pnl`] = 0;
    elements.push({
        elementId: `${l}-cstm-pnl`,
        text: "Custom Panel",
        // @ts-ignore
        location: l,
        type: "panel",
        actions: [
            {
                eventType: "onrender",
                eventHandler: (detail: ExtensibilityEventDetail) => {
                    renderContent("panel", detail, `${l}-cstm-pnl`);

                    trados.updateElement(
                        `${l}-cstm-pnl`,
                        // todo: when publishing new public package version, use "title" for containers instead of "text"
                        {
                            text: "(updated `\"'~!@#$%^&*()_+-=:?/><;,.\\|ăîâșț) Custom Panel with extra long long long long long long long long long long long long long long long long long long long long long long long long long long long long text"
                        }
                        //update: { hidden: true }
                    );
                },
                payload: []
            }
        ]
    });
});

const allTabLocations = [
    // projects
    "project-details-tabpanel",
    // task-inbox
  "task-tabpanel"
];

allTabLocations.forEach(l => {
    renderCounter[`${l}-cstm-tab`] = 0;
    elements.push({
        elementId: `${l}-cstm-tab`,
        text: "Custom Tab",
        // @ts-ignore
        location: l,
        type: "tab",
        actions: [
            {
                eventType: "onrender",
                eventHandler: (detail: ExtensibilityEventDetail) => {
                    renderContent("tab", detail, `${l}-cstm-tab`);

                    trados.updateElement(
                        `${l}-cstm-tab`,
                        // todo: when publishing new public package version, use "title" for containers instead of "text"
                        {
                            text: "(updated `\"'~!@#$%^&*()_+-=:?/><;,.\\|ăîâșț) Custom Tab with extra long long long long long long long long long long long long long long long long long long long long long long long long long long long long text"
                        }
                        //update: { hidden: true }
                    );
                },
                payload: []
            }
        ]
    });
});

const allSidebarBoxLocations = [
    // projects
    "project-details-dashboard-sidebar",
    "project-details-task-history-sidebar",
    // task-inbox
    "new-tasks-list-sidebar",
    "active-tasks-list-sidebar",
    "completed-tasks-list-sidebar",
    "task-sidebar"
];

allSidebarBoxLocations.forEach(l => {
    renderCounter[`${l}-cstm-sb`] = 0;
    elements.push({
        elementId: `${l}-cstm-sb`,
        text: "Custom Sidebar Box",
        // @ts-ignore
        location: l,
        type: "sidebarBox",
        actions: [
            {
                eventType: "onrender",
                eventHandler: (detail: ExtensibilityEventDetail) => {
                    renderContent("sidebarBox", detail, `${l}-cstm-sb`);

                    trados.updateElement(
                        `${l}-cstm-sb`,
                        // todo: when publishing new public package version, use "title" for containers instead of "text"
                        {
                            text: "(updated `\"'~!@#$%^&*()_+-=:?/><;,.\\|ăîâșț) Custom Sidebar Box with extra long long long long long long long long long long long long long text"
                        }
                        //update: { hidden: true }
                    );
                },
                payload: []
            }
        ]
    });
});
*/

// onReady function calls publish("register", configWithElementsAndEventHandlers)
// then, once registered, performs callback function passing extensionKey, tenant and token as registrationResult arg
trados.onReady(elements, () => {
    // extension registered
    // extensionKey retrieved for identification if events communication
    // tenant & token available for API calls
    console.log("[UI Extensibility] [extension] Extension registered.");
});

console.log(
    "[UI Extensibility] [extension] Register request event published. External script loaded."
);
