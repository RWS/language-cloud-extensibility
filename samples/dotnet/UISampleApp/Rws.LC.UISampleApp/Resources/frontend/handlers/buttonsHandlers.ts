import { trados, ExtensibilityEventDetail, Project, tradosProjectTemplateApi } from "@trados/trados-ui-extensibility";
import { logExtensionData } from "./helpers";

/**
 * Handles the callAppApiButton's click event.
 * Calls my own app's greeting API to receive the greeting message which is included in a notification.
 *
 * @param detail The event detail object.
 */
export const callAppApiButtonClicked = (
    detail: ExtensibilityEventDetail
) => {
    logExtensionData(detail);

    trados
        // The callAppApi function will automatically add headers: the account identifier and authorization token.
        .callAppApi({
            url: `api/greeting/`,
            method: "GET"
        })
        .then(data => {
            trados.showNotification(
                `App backend says <b>${data?.responseData?.greeting}</b>`,
                trados.contexts.projects,
                trados.notificationTypes.success
            );
        })
        .catch(reason => {
            console.error("[UI Extensibility] [my UI extension] Failed to call my app's API", reason);
            trados.showNotification(
                "Failed to call my app's API.",
                trados.contexts.projects,
                trados.notificationTypes.fail
            );
        });
}

/**
 * Handles the callPublicApiButton's render event.
 * Checks whether the current project is based on a project template, then updates callPublicApiButton's hidden property depending on the outcome.
 *
 * @param detail The event detail object.
 */
export const callPublicApiButtonRendered = (
    detail: ExtensibilityEventDetail
) => {
    logExtensionData(detail);
    const hidden = !detail.project?.projectTemplate;
    trados.updateElement("callPublicApiButton", { hidden: hidden });
};

/**
 * Handles the callPublicApiButton's click event.
 * Calls the ProjectTemplateAPI from Language Cloud Public API to get the current project's project template and displays its name in a notification.
 *
 * @param detail The event detail object.
 */
export const callPublicApiButtonClicked = (detail: ExtensibilityEventDetail) => {
    logExtensionData(detail);

    const projectTemplateId = detail.project?.projectTemplate?.id;
    if (projectTemplateId) {
        // Call the function that initializes the tradosProjectTemplateApi.
        tradosProjectTemplateApi()
            // Call the getProjectTemplate API endpoint.
            .getProjectTemplate({
                // Provide the project template identifier.
                projectTemplateId: projectTemplateId,
                // Provide the account identifier and authorization token returned by the getRegistrationResult function.
                ...trados.getRegistrationResult()
            })
            .then(apiData => {
                console.log("[UI Extensibility] [my UI extension] Project template details from Language Cloud Public API", apiData);
                
                trados.showNotification(
                    `This project was created using the <b>${apiData.name}</b> project template`,
                    trados.contexts.projects,
                    trados.notificationTypes.success
                );
            })
            .catch(e => {
                console.error(`[UI Extensibility] [my UI extension] API call failed`, e );
            });
    }
}

/**
 * Handles the myNavigateButton's render event.
 * Checks whether the current project is based on a project template, then updates myNavigateButton's hidden property depending on the outcome.
 *
 * @param detail The event detail object.
 */
export const myNavigateButtonRendered = (
    detail: ExtensibilityEventDetail
) => {
    logExtensionData(detail);
    const hidden = !detail.project?.projectTemplate;
    trados.updateElement("myNavigateButton", { hidden: hidden });
};

/**
 * Handles the myNavigateButton's click event.
 * Navigates to the current project's project template details view.
 *
 * @param detail The event detail object.
 */
export const myNavigateButtonClicked = (
    detail: ExtensibilityEventDetail
) => {
    logExtensionData(detail);
    if (detail.project?.projectTemplate) {
        const projectTemplateId = detail.project.projectTemplate.id;
        const projectTemplatePath = `resources/project-templates/${projectTemplateId}`;
        trados.navigate(projectTemplatePath, trados.navigationTypes.route);
    }
};

/**
 * Handles the myGetUiDataButton's click event.
 * Gets the currently active tab in the project details view from the event detail; the onclick action's payload is ["projectActiveTab"].
 * Gets the currently selected projects in the projects list view using the getLocalData function as an alternative to the action payload approach.
 * Shows a notification containing both the active tab and the selected projects in the projects list.
 *
 * @param detail The event detail object.
 */
export const myGetUiDataButtonClicked = (detail: ExtensibilityEventDetail) => {
    logExtensionData(detail);

    // The click event detail contains local data requested via onclick action's payload - see extension elements array in index.ts.
    const myActiveTab = detail.projectActiveTab;

    // You can also request local UI data using the trados.getLocalData function.
    trados
        .getLocalData(trados.contexts.projects, trados.dataSelectors.selectedProjects)
        .then((data: { selectedProjects: Project[] }) => {
            const mySelectedProjects = data.selectedProjects;
            const mySelectedProjectsCount = mySelectedProjects.length;
            const notificationData = [
                `My active tab is <b>${myActiveTab}</b>.`,
                mySelectedProjectsCount
                    ? `My selected projects (${mySelectedProjectsCount}):<br>${mySelectedProjects.map(p => " - " + p.name).join("<br>")}`
                    : "No selected projects"
            ];
            trados.showNotification(notificationData.join("<br><br>"), trados.contexts.projects)
        })
        .catch(reason => {
            console.error("[UI Extensibility] [my UI extension] Failed to get local data", reason);
            trados.showNotification(
                "Failed to get local data.",
                trados.contexts.projects,
                trados.notificationTypes.fail
            );
        });
};
