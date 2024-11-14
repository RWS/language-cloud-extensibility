// Import the trados object and the ExtensionElement and ExtensibilityEventDetail types from the trados-ui-extensibility.
import { trados, ExtensionElement, ExtensibilityEventDetail } from "@trados/trados-ui-extensibility";

// Import my extension's event handlers.
import { myCustomPanelRendered, myCustomSidebarBoxRendered, myCustomTabRendered } from "./handlers/panelsHandlers"
import { myNavigateButtonRendered, myNavigateButtonClicked, myGetUiDataButtonClicked, callAppApiButtonClicked, callPublicApiButtonRendered, callPublicApiButtonClicked } from "./handlers/buttonsHandlers";

// First create the extension elements array which describes the custom elements that will be added in the UI.
const elements: ExtensionElement[] = [
    // Button that calls my own app's API.
    // The API endpoint code is available in Controllers/GreetingController.cs.
    // A notification is shown containing a greeting message.
    {
        elementId: "callAppApiButton",
        icon: "x-fal fa-smile",
        text: "Hello, App!",
        location: "projects-list-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: callAppApiButtonClicked,
                payload: ["project"]
            }
        ]
    },

    // Link button - button with isLink property set to true.
    // Notice there is no "action" property specified.
    // The "href" property acts similarly to the href attribute of an HTML anchor element and the "href" URL gets opened in a new tab.
    {
        elementId: "myLinkButton",
        // Custom buttons with isLink: true are automatically displayed with an "external-link" icon aligned to the right of the button.
        // You can override this default behaviour by explicitly setting the "icon"" or "iconAlign"" properties.
        icon: "x-fal fa-book",
        iconAlign: "left",
        text: "Extensibility Docs",
        location: "projects-list-toolbar",
        type: "button",
        isLink: true,
        href: "https://languagecloud.sdl.com/lc/extensibility-docs"
    },

    // Button that calls the Language Cloud Public API.
    // Hidden initially; only shown for projects created based on a project template.
    // Shows a notification containing the name of the project template associated with the current project.
    {
        elementId: "callPublicApiButton",
        icon: "x-fal fa-info",
        text: "Show Project Template Name",
        location: "project-details-toolbar",
        type: "button",
        hidden: true,
        actions: [
            {
                eventType: "onrender",
                eventHandler: callPublicApiButtonRendered,
                // Specific to onrender: the event handler is called with a default data-selector included in the event detail.
                // The default data-selector depends on context; in this case, it's the current project details.
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: callPublicApiButtonClicked,
                payload: ["project"]
            }
        ]
    },
    
    // Button that navigates to the project's template details view.
    // Hidden initially; only shown for projects created based on a project template.
    // In the onrender event handler, myNavigateButtonRendered, the button's hidden property is updated depending on whether the current project has a project template or not.
    {
        elementId: "myNavigateButton",
        icon: "x-fal fa-location-arrow",
        text: "Navigate to Project Template",
        location: "project-details-toolbar",
        type: "button",
        hidden: true,
        actions: [
            {
                eventType: "onrender",
                eventHandler: myNavigateButtonRendered,
                // Specific to onrender: even if the payload for the onrender event is an empty array,
                // the event handler gets called with a default data-selector included in the event detail.
                // The default data-selector depends on context and location; in this case, it's the current project details.
                payload: []
            },
            {
                eventType: "onclick",
                eventHandler: myNavigateButtonClicked,
                payload: ["project"]
            }
        ]
    },
    
    // Button that retrieves a portion of the data available in the UI and shows a notification in the top right of the view.
    // The notification will display
    //  - the active tab in the project details view (retrieved via the onrender action's payload) and
    //  - the selected projects in the projects list view (retrieved using the trados.getLocalData function).
    {
        elementId: "myGetUiDataButton",
        icon: "x-fal fa-hand-pointer",
        text: "Get UI Data",
        location: "project-details-toolbar",
        type: "button",
        actions: [
            {
                eventType: "onclick",
                eventHandler: myGetUiDataButtonClicked,
                payload: ["projectActiveTab"]
            }
        ]
    },

    // Dropdown button that updates the properties of the next button (myTargetButton).
    {
        elementId: "myDropdownButton",
        icon: "x-fal fa-chevron-right",
        text: "Update Target Button",
        location: "project-details-toolbar",
        type: "button",
        menu: [
            // Toggle myTargetButton's hidden property.
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

            // Toggle myTargetButton's disabled property.
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
            
            // Toggle myTargetButton's icon.
            {
                icon: "x-fal fa-angle-right",
                text: "Set star icon",
                value: "staricon"
            },
            {
                icon: "x-fal fa-angle-right",
                text: "Set dot circle icon",
                value: "dotcircleicon"
            },
            {
                separator: true
            },

            // Toggle multiple properties in myTargetButton's menu options.
            {
                icon: "x-fal fa-angle-right",
                text: "Disable & make uppercase & set star icon for dropdown menu options",
                value: "disablemenuoptions"
            },
            {
                icon: "x-fal fa-angle-right",
                text: "Enable & make default case & set angle icon for dropdown menu options",
                value: "enablemenuoptions"
            }
        ],
        actions: [
            {
                eventType: "onclick",
                eventHandler: (detail: ExtensibilityEventDetail) => {
                    // The detail.value is the value of the clicked button dropdown menu option.
                    switch (detail.value) {
                        case "hide":
                            trados.updateElement("myTargetButton", { hidden: true });
                            break;
                        case "show":
                            trados.updateElement("myTargetButton", { hidden: false });
                            break;
                        case "disable":
                            trados.updateElement("myTargetButton", { disabled: true });
                            break;
                        case "enable":
                            trados.updateElement("myTargetButton", { disabled: false });
                            break;
                        case "staricon":
                            trados.updateElement("myTargetButton", { icon: "x-fal fa-star" });
                            break;
                        case "dotcircleicon":
                            trados.updateElement("myTargetButton", { icon: "x-fal fa-dot-circle" });
                            break;
                        case "disablemenuoptions":
                            trados.updateElement("myTargetButton", {
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
                            trados.updateElement("myTargetButton", {
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

    // The target button has no actions.
    // Its properties get updated when the previous button's (myDropdownButton) dropdown menu options are clicked.
    {
        elementId: "myTargetButton",
        icon: "x-fal fa-dot-circle",
        text: "Target Button",
        location: "project-details-toolbar",
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

    // Custom tab.
    {
        elementId: "myCustomTab",
        text: "My Custom Tab",
        location: "project-details-tabpanel",
        type: "tab",
        actions: [
            {
                eventType: "onrender",
                eventHandler: myCustomTabRendered,
                payload: []
            }
        ]
    },

    // Custom panel.
    {
        elementId: "myCustomPanel",
        text: "My Custom Panel",
        location: "project-details-dashboard-main",
        type: "panel",
        actions: [
            {
                eventType: "onrender",
                eventHandler: myCustomPanelRendered,
                payload: []
            }
        ]
    },

    // Custom sidebarBox.
    {
        elementId: "dashboardSidebarBox",
        text: "My Custom Sidebar Box",
        location: "project-details-dashboard-sidebar",
        type: "sidebarBox",
        actions: [
            {
                eventType: "onrender",
                eventHandler: myCustomSidebarBoxRendered,
                payload: []
            }
        ]
    }
];

// Then call trados.onReady function to register the UI extension with Trados UI.
trados.onReady(
    // The first argument is the extension elements array so Trados UI known what custom elements to add and where.
    elements,
    // The second argument is a callback function.
    () => {
        // My UI extension is now registered with Trados UI.
        // The extensionKey is available for identification during events communication between Trados UI and UI extension.
        // The account identifier & authorization token are available for API calls.
        console.log("[UI Extensibility] [my UI extension] UI extension registered with Trados UI.");
    });

console.log("[UI Extensibility] [my UI extension] External script loaded.");
