import { trados } from "@sdl/extensibility"; // doesn't work until new extensibility public package published
//import { trados } from "../../../../../../../public-packages/extensibility/src/index"; // works
import { ExtensibilityEventDetail } from "@sdl/extensibility-types/extensibility";
import { Project } from "@sdl/extensibility/lib/lc-public-api/models";
import { logExtensionData } from "./helpers";

let project: Project | undefined = undefined;
let selectedProjects: Project[] | undefined = undefined;

export const extensionsHelperTabRendered = (
  detail: ExtensibilityEventDetail
) => {
  logExtensionData(detail);

  // extensionHelper is the elementId of the tab (in activated-extension.json config)
  const tabContentWrapper = document.getElementById(detail.domElementId);
  if (tabContentWrapper) {
    // reset content for rerenders
    tabContentWrapper.innerHTML = "";

    // create heading
    const heading = document.createElement("div");
    heading.className = "x-panel-header-title-light-framed";
    heading.setAttribute("style", "margin-bottom: 10px");
    heading.innerText = "Available data";

    //project and selectedProjects selectors set as event payload
    project = detail.project;
    selectedProjects = detail.selectedProjects;
    // create all data object
    const data = {
      project: project,
      selectedProjects: selectedProjects
    };

    // create copy to clipboard button
    const copyBtn = document.createElement("a");

    //copyBtn.className = "x-btn x-btn-default-toolbar-medium";
    copyBtn.setAttribute("style", "margin-left: 10px; color: #434a65;");
    copyBtn.innerHTML = `<span class="x-fa fa-copy"></span>`;
    copyBtn.onclick = () => {
      // @ts-ignore
      SDL.common.utils.ClipboardCopy.copyToClipboard(JSON.stringify(data));
      if (!copyBtn.className.indexOf("fa-check")) {
        copyBtn.className = "x-fa fa-check";
        setTimeout(() => {
          copyBtn.className = "x-fa fa-copy";
        }, 2000);
      }
    };

    // add copy button to heading and heading to tab
    heading.appendChild(copyBtn);
    tabContentWrapper.appendChild(heading);
    tabContentWrapper.style.position = "relative";

    // json-viewer css
    const jsonViewerStyle = document.createElement("style");
    //jsonViewerStyle.setAttribute("data-loaded-by-extension-id", key || ""); todo: may be helpful for debugging, but extension key is not exposed. should it be?
    jsonViewerStyle.innerHTML = getJsonViewerCss();
    document.body.appendChild(jsonViewerStyle);

    // json-viewer js
    // TODO: investigate importing JSONViewer to prevent including large code
    // JSONViewer - by Roman Makudera 2016 (c) MIT licence.
    var JSONViewer = (function (document) {
      var Object_prototype_toString = {}.toString;
      var DatePrototypeAsString = Object_prototype_toString.call(new Date());

      /** @constructor */
      function JSONViewer() {
        // @ts-ignore
        this._dom_container = document.createElement("pre");
        // @ts-ignore
        this._dom_container.classList.add("json-viewer");
      }

      /**
       * Visualise JSON object.
       *
       * @param {Object|Array} json Input value
       * @param {Number} [inputMaxLvl] Process only to max level, where 0..n, -1 unlimited
       * @param {Number} [inputColAt] Collapse at level, where 0..n, -1 unlimited
       */
      JSONViewer.prototype.showJSON = function (
        jsonValue: any,
        inputMaxLvl: number,
        inputColAt: number
      ) {
        // Process only to maxLvl, where 0..n, -1 unlimited
        var maxLvl = typeof inputMaxLvl === "number" ? inputMaxLvl : -1; // max level
        // Collapse at level colAt, where 0..n, -1 unlimited
        var colAt = typeof inputColAt === "number" ? inputColAt : -1; // collapse at
        this._dom_container.innerHTML = "";
        walkJSONTree(this._dom_container, jsonValue, maxLvl, colAt, 0, "");
      };
      /**
       * Get container with pre object - this container is used for visualise JSON data.
       *
       * @return {Element}
       */
      JSONViewer.prototype.getContainer = function () {
        return this._dom_container;
      };

      function createCopyLinks(
        path: string,
        value: any,
        extraContainerCssClass?: string
      ) {
        const copyPathBtn = document.createElement("a");
        copyPathBtn.title = path;
        copyPathBtn.innerText = "Copy path";
        copyPathBtn.setAttribute("style", "margin-left: 10px");
        copyPathBtn.onclick = () => {
          //debugger;
          // @ts-ignore
          SDL.common.utils.ClipboardCopy.copyToClipboard(path);
        };
        const copyValueBtn = document.createElement("a");
        const stringValue =
          typeof value === "object" ? JSON.stringify(value) : value;
        copyValueBtn.title = stringValue;
        copyValueBtn.innerText = "Copy value";
        copyValueBtn.setAttribute("style", "margin-left: 10px");
        copyValueBtn.onclick = () => {
          //debugger;
          // @ts-ignore
          SDL.common.utils.ClipboardCopy.copyToClipboard(stringValue);
        };
        const spanEl = document.createElement("span");
        spanEl.className =
          "copy-links" +
          (extraContainerCssClass ? " " + extraContainerCssClass : " simple");
        spanEl.appendChild(copyPathBtn);
        spanEl.appendChild(copyValueBtn);
        return spanEl;
      }

      /**
       * Recursive walk for input value.
       *
       * @param {Element} outputParent is the Element that will contain the new DOM
       * @param {Object|Array} value Input value
       * @param {Number} maxLvl Process only to max level, where 0..n, -1 unlimited
       * @param {Number} colAt Collapse at level, where 0..n, -1 unlimited
       * @param {Number} lvl Current level
       * @param {String} path Current object path
       */
      function walkJSONTree(
        outputParent: any,
        value: any,
        maxLvl: number,
        colAt: number,
        lvl: number,
        path: string
      ) {
        var isDate =
          Object_prototype_toString.call(value) === DatePrototypeAsString;
        var realValue =
          !isDate &&
          typeof value === "object" &&
          value !== null &&
          "toJSON" in value
            ? value.toJSON()
            : value;
        if (typeof realValue === "object" && realValue !== null && !isDate) {
          var isMaxLvl = maxLvl >= 0 && lvl >= maxLvl;
          var isCollapse = colAt >= 0 && lvl >= colAt;
          var isArray = Array.isArray(realValue);
          var items = isArray ? realValue : Object.keys(realValue);
          if (lvl === 0) {
            // root level
            var rootCount = _createItemsCount(items.length);
            // hide/show
            var rootLink = _createLink(isArray ? "[" : "{");
            if (items.length) {
              rootLink.addEventListener("click", function () {
                if (isMaxLvl) return;
                rootLink.classList.toggle("collapsed");
                rootCount.classList.toggle("hide");
                // main list
                outputParent.querySelector("ul").classList.toggle("hide");
              });
              if (isCollapse) {
                rootLink.classList.add("collapsed");
                rootCount.classList.remove("hide");
              }
            } else {
              rootLink.classList.add("empty");
            }
            rootLink.appendChild(rootCount);
            outputParent.appendChild(rootLink); // output the rootLink
          }
          if (items.length && !isMaxLvl) {
            var len = items.length - 1;
            var ulList = document.createElement("ul");
            ulList.setAttribute("data-level", lvl.toString());
            ulList.classList.add("type-" + (isArray ? "array" : "object"));
            items.forEach(function (key: any, ind: number) {
              var item = isArray ? key : value[key];
              var li = document.createElement("li");
              if (typeof item === "object") {
                let index = -1;
                let pathToCopy = path + (path.length ? "." : "") + key;

                // null && date
                if (!item || item instanceof Date) {
                  li.appendChild(
                    document.createTextNode(isArray ? "" : key + ": ")
                  );
                  li.appendChild(createSimpleViewOf(item ? item : null, true));
                }
                // array & object
                else {
                  var itemIsArray = Array.isArray(item);
                  var itemLen = itemIsArray
                    ? item.length
                    : Object.keys(item).length;

                  index = realValue.indexOf ? realValue.indexOf(item) : -1;
                  pathToCopy =
                    path +
                    (path.length
                      ? index > -1
                        ? "[" + index + "]"
                        : "." + key
                      : "" + key);

                  // empty
                  if (!itemLen) {
                    li.appendChild(
                      document.createTextNode(
                        key + ": " + (itemIsArray ? "[]" : "{}")
                      )
                    );
                  } else {
                    // 1+ items
                    var itemTitle =
                      (typeof key === "string" ? key + ": " : "") +
                      (itemIsArray ? "[" : "{");
                    var itemLink = _createLink(itemTitle);
                    var itemsCount = _createItemsCount(itemLen);
                    // maxLvl - only text, no link
                    if (maxLvl >= 0 && lvl + 1 >= maxLvl) {
                      li.appendChild(document.createTextNode(itemTitle));
                    } else {
                      itemLink.appendChild(itemsCount);
                      li.appendChild(itemLink);
                    }
                    //debugger;
                    walkJSONTree(li, item, maxLvl, colAt, lvl + 1, pathToCopy);
                    li.appendChild(
                      document.createTextNode(itemIsArray ? "]" : "}")
                    );
                    var list = li.querySelector("ul");
                    var itemLinkCb = function () {
                      itemLink.classList.toggle("collapsed");
                      itemsCount.classList.toggle("hide");
                      list!.classList.toggle("hide");
                    };
                    // hide/show
                    itemLink.addEventListener("click", itemLinkCb);
                    // collapse lower level
                    if (colAt >= 0 && lvl + 1 >= colAt) {
                      itemLinkCb();
                    }
                  }
                }
                // add comma to the end
                if (ind < len) {
                  li.appendChild(document.createTextNode(","));
                }

                if (key === "lastModifiedAt") {
                  //debugger;
                }
                if (
                  Object_prototype_toString.call(item) === DatePrototypeAsString
                ) {
                  li.appendChild(
                    createCopyLinks(path + (path.length ? "." : "") + key, item)
                  );
                } else {
                  li.insertBefore(
                    createCopyLinks(pathToCopy, item, "expanded"),
                    li.children[1]
                  );
                  li.appendChild(
                    createCopyLinks(pathToCopy, item, "collapsed")
                  );
                }
              }
              // simple values
              else {
                // object keys with key:
                if (!isArray) {
                  li.appendChild(document.createTextNode(key + ": "));
                }
                // recursive
                walkJSONTree(
                  li,
                  item,
                  maxLvl,
                  colAt,
                  lvl + 1,
                  path + (path.length ? "." : "") + key
                );
                // add comma to the end
                if (ind < len) {
                  li.appendChild(document.createTextNode(","));
                }
                li.appendChild(
                  createCopyLinks(path + (path.length ? "." : "") + key, item)
                );
              }

              ulList.appendChild(li);
              // @ts-ignore
            }, this);
            outputParent.appendChild(ulList); // output ulList
          } else if (items.length && isMaxLvl) {
            var itemsCount = _createItemsCount(items.length);
            itemsCount.classList.remove("hide");
            outputParent.appendChild(itemsCount); // output itemsCount
          }
          if (lvl === 0) {
            // empty root
            if (!items.length) {
              var itemsCount = _createItemsCount(0);
              itemsCount.classList.remove("hide");
              outputParent.appendChild(itemsCount); // output itemsCount
            }
            // root cover
            outputParent.appendChild(
              document.createTextNode(isArray ? "]" : "}")
            );
            // collapse
            if (isCollapse) {
              outputParent.querySelector("ul").classList.add("hide");
            }
          }
        } else {
          // simple values
          outputParent.appendChild(createSimpleViewOf(value, isDate));
        }
      }

      /**
       * Create simple value (no object|array).
       *
       * @param  {Number|String|null|undefined|Date} value Input value
       * @return {Element}
       */
      function createSimpleViewOf(value: any, isDate: boolean) {
        var spanEl = document.createElement("span");
        var type: any = typeof value;
        var asText = "" + value;
        if (type === "string") {
          asText = '"' + value + '"';
        } else if (value === null) {
          type = "null";
          //asText = "null";
        } else if (isDate) {
          type = "date";
          asText = value.toLocaleString();
        }
        spanEl.className = "type-" + type;
        spanEl.textContent = asText;
        return spanEl;
      }

      /**
       * Create items count element.
       *
       * @param  {Number} count Items count
       * @return {Element}
       */
      function _createItemsCount(count: number) {
        var itemsCount = document.createElement("span");
        itemsCount.className = "items-ph hide";
        itemsCount.innerHTML = _getItemsTitle(count);
        return itemsCount;
      }

      /**
       * Create clickable link.
       *
       * @param  {String} title Link title
       * @return {Element}
       */
      function _createLink(title: string) {
        var linkEl = document.createElement("a");
        linkEl.classList.add("list-link");
        linkEl.href = "javascript:void(0)";
        linkEl.innerHTML = title || "";
        return linkEl;
      }

      /**
       * Get correct item|s title for count.
       *
       * @param  {Number} count Items count
       * @return {String}
       */
      function _getItemsTitle(count: number) {
        var itemsTxt = count > 1 || count === 0 ? "items" : "item";
        return count + " " + itemsTxt;
      }

      return JSONViewer;
    })(document);

    // @ts-ignore
    var jsonViewer = new JSONViewer();
    tabContentWrapper.appendChild(jsonViewer.getContainer());
    jsonViewer.showJSON(data, null, 1);
    console.log("[UI Extensibility] [extension] JSON data displayed");

    // add playground
    const playgroundHeading = document.createElement("div");
    playgroundHeading.className = "x-panel-header-title-light-framed";
    playgroundHeading.setAttribute("style", "margin: 40px 0 5px");
    playgroundHeading.innerText = "Dev Playground";
    tabContentWrapper.appendChild(playgroundHeading);

    const playgroundForm = document.createElement("form");
    playgroundForm.autocomplete = "off";
    const hidden = document.createElement("input");
    hidden.setAttribute("type", "hidden");
    hidden.setAttribute("autocomplete", "false");
    playgroundForm.appendChild(hidden);

    const label = document.createElement("label");
    label.setAttribute("for", "extension-notification-input");
    label.setAttribute("autocomplete", "false");
    label.setAttribute(
      "style",
      "margin-bottom: 10px; font-weight: 700; display: inline-block; width: 120px;"
    );
    label.innerText = "Notification text";
    playgroundForm.appendChild(label);

    const input = document.createElement("input");
    input.setAttribute("id", "extension-notification-input");
    input.setAttribute(
      "style",
      "margin: 0 5px; border: 1px solid #9ba9b6; border-radius: 4px; padding: 5px 10px 4px; width: 475px;"
    );
    input.placeholder = "Input notification text";
    playgroundForm.appendChild(input);

    playgroundForm.appendChild(document.createElement("br"));

    const typeLabel = document.createElement("label");
    typeLabel.setAttribute("for", "extension-notification-select");
    typeLabel.setAttribute(
      "style",
      "margin-bottom: 10px; font-weight: 700; display: inline-block; width: 120px;"
    );
    typeLabel.innerText = "Notification type";
    playgroundForm.appendChild(typeLabel);

    const select = document.createElement("select");
    select.setAttribute("id", "extension-notification-select");
    select.setAttribute(
      "style",
      "margin: 0 5px; border: 1px solid #9ba9b6; border-radius: 4px; padding: 5px 6px 4px; width: 475px;"
    );
    [
      ["Information", "info"],
      ["Success", "success"],
      ["Warning", "warning"],
      ["Failure", "fail"]
    ].forEach(type => {
      const option = document.createElement("option");
      option.textContent = type[0];
      option.value = type[1];
      select.appendChild(option);
    });
    playgroundForm.appendChild(select);

    playgroundForm.appendChild(document.createElement("br"));

    const button = document.createElement("button");
    button.type = "submit";
    button.innerText = "Display notification";
    // @ts-ignore
    button.style = "margin-top: 5px";
    button.className = "x-btn x-btn-default-toolbar-medium";
    playgroundForm.appendChild(button);

    playgroundForm.addEventListener("submit", function (submitEvent) {
      // sample input where eval was needed: "This is the <b>" + projectDetails.name + "</b> project."
      //const text = input.value.indexOf('"') > -1 || input.value.indexOf("`") > -1
      //    ? eval(input.value.replace(/projectDetails/g, "project"))
      //    : input.value;
      const text = input.value;
      const type = select.value as "info" | "success" | "warning" | "fail";
      trados.showNotification(text, trados.contexts.projects, type);

      submitEvent.preventDefault();
    });

    const scriptText = `trados.showNotification(
  "{text}",
  trados.contexts.projects,
  "{type}"
});`;
    const code = document.createElement("div");
    // @ts-ignore
    code.style =
      "white-space: pre; overflow: auto; margin-top: 10px; border: 1px solid #9ba9b6; border-radius: 4px; padding: 5px 10px 4px; width: 600px; height: 170px; ";
    playgroundForm.appendChild(code);
    const updateCode = () => {
      const text = input.value;
      code.innerText = scriptText
        .replace("{type}", select.value)
        .replace("{text}", text);
      /*.replace(
                          "{text}",
                          (text.indexOf('"') > -1 || text.indexOf("`") > -1
                            ? text
                            : `"${text}"`) || '""'
                        )
                        */
    };

    input.addEventListener("input", () => {
      updateCode();
    });
    select.addEventListener("change", () => {
      updateCode();
    });
    updateCode();

    tabContentWrapper.appendChild(playgroundForm);
  }
};

const getJsonViewerCss = () => {
  return `
.json-viewer {
  color: #000;
  margin: 0;
  padding-left: 20px;
  line-height: 20px;
  background-image: linear-gradient(
    0deg,
    #fcfcfc 50%,
    #f3f3f3 50%,
    #f3f3f3 100%
  );
  background-size: 40px 40px;
}
.json-viewer ul {
  list-style-type: none;
  margin: 0;
  margin: 0 0 0 1px;
  border-left: 1px dotted #ccc;
  padding-left: 2em;
}
.json-viewer .hide {
  display: none;
}
.json-viewer .type-string {
  color: #0b7500;
}
.json-viewer .type-date {
  color: #cb7500;
}
.json-viewer .type-boolean {
  color: #1a01cc;
  font-weight: bold;
}
.json-viewer .type-number {
  color: #1a01cc;
}
.json-viewer .type-null,
.json-viewer .type-undefined {
  color: #90a;
}
.json-viewer a.list-link {
  color: #000;
  text-decoration: none;
  position: relative;
}
.json-viewer a.list-link:before {
  color: #aaa;
  content: "▼";
  position: absolute;
  display: inline-block;
  width: 1em;
  left: -1em;
}
.json-viewer a.list-link.collapsed:before {
  content: "►";
}
.json-viewer a.list-link.empty:before {
  content: "";
}
.json-viewer .items-ph {
  color: #aaa;
  padding: 0 1em;
}
.json-viewer .items-ph:hover {
  text-decoration: underline;
}
.json-viewer .copy-links {
  display: none;
}
.json-viewer li:hover > a.list-link.collapsed ~ .copy-links.collapsed,
.json-viewer li:hover > a.list-link:not(.collapsed) ~ .copy-links.expanded,
.json-viewer li:hover > .copy-links.simple {
  display: inline;
}`;
};
