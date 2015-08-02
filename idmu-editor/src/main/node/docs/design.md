# Table of Contents

- [Overview](#overview)
- [Component BOM](#bom)
- [App](#app)
- [TemplateCollection](#template_collection)
- [Ribbon](#template_ribbon)
- [RibbonItem](#template_ribbon_item)
- [TemplateEditor](#template_editor)
  - [Handling Sub-Templates](#bookmarks)
  - [Handling Save](#handle_save_action)
- [TemplateHeader](#template_header)
- [HeaderPanel (Accordion)](#header_panel)
- [Directives](#directives)
  - [Drag and Drop](#drag_and_drop)
- [DirectivesEditor] (#directives_editor)
- [TemplateBody] (#template_bodyp)
- [Implementation Detais] (#implementation_detail)
  - [Callbacks] (#callbacks)
  - [Nesting Levels & Index] (#levels_index)
  - [jQuery] (#jQuery)
  - [Utils] (#utils)
- [Screen Shots] (#screen_shots)


<a name="overview"/>
# Overview

This document describes the main components of the IDMU template editor application.

<a name="bom"/>
# IDMU Template Editor Bill of Materials
```
App(d)
  TemplateCollection(d)
  Ribbon(d)
   RibbonItem(d)
    TemplateEditor(d)
     TemplateHeader
       Actions[Add,Remove,Save]
       HeaderPanel
         Edit[name, description, output]
         SelectList[directives]
         Configure[button]
         
       >>>> OR <<<
       
       Directives
         LHSList (available all directives, DnD source)
         RHSList (configured directives, DnD target)
          [<li>DirectivesEditorTrigger</li> ...]
          DirectivesEditorTrigger
           Actions [edit, remove]
          DirectiveEditors (modal)
           RequireTags
           ReplaceValue
           InsertSubTemplatesFromTagData
           ...
           ..
           
     TemplateBody
       ContentEditable
       InsertBookmarkTrigger
         InsertBookmark (modal)

     >>>> OR <<<
     
     [App, ...]
     
```

<a name="app"/>
## App Component

The App component encapsulates the top top-level state of the IDMU template editor, and all
interactions with the server. It is also the primary source of
render trigger due to state changes. The component is meant to
be embedded inside other components in a nested fashion.

The important top-level states are:

```
- global directives
- top-level collection
- selected collection
- selected ribbon item
- templates for selected collection
- current template in view
```

The important server interaction methods are:

```
- loadDirectives
- loadCollections
- loadTemplates
- loadTemplate
- addTemplate
- removeTemplate
- saveTemplate
```

<a name="template_collection"/>
## TemplateCollection

This component encapsulates the selection behavior of top-level templates.
Note that this component is never utilized when the `App` component
is nested. This logic is implemented in the `App:header` method.


<a name="template_ribbon"/>
## TemplateRibbon

The ribbon class acts as a container for a group of templates in
a selected collection. The behavior is like a cover-flow showing a
single template at a time. The component provides left/right navigation.

The IDUMU template editor embeds a `TemplateRibbonItem` for each template
in the application. The template itself is lazily loaded when the item
is in view.

When you click the left/right buttons, the app calls `App:collectionSelected.


<a name="template_ribbon_item"/>
## TemplateRibbonItem

This class encapsulates ribbon related behavior and embeds the
`TemplateEditor` component for a particular template.

<a name="template_editor"/>
## TemplateEditor

This class acts as container for multiple components as stated
in the BOM. This class also acts as a narrow channel for template
save actions.


<a name="bookmarks"/>
### Handling Of Bookmarks

The following actions take place when the IDMU renders this component:

- The `TemplateHeader` is instantiated and rendered.

- Next `template.content` is chunked into `body_items`. The chunking happens
at the boundries of detected bookmarks `<tkBookmark/>` tags.

- Each item in the collection can either be of type `text` or of
type `bookmark` with associated data attribute (i.e. text or tag with
attributes)

- Finally the `body_items` are traversed and based on the type of
the item either a `TemplateBody` is instantiated or an `App` is
instantiated.


<a name="handle_save_action"/>
### Handling Of Save Action

When user executes a save action by either pressing the `Save` button
or when a user inserts a bookmark, the `TemplateEditor:handleSave` method is
called. The following sequence of action happens.

- The header values are collected

- The configured directives are collected

- The `template.content` text is re-constructed using newly introduced
text content and/or any newly inserted bookmark.

- Finally the `App:saveTemplate` method is invoked.


<a name="template_header"/>
## TemplateHeader

This header component provides for the following.

- Embeds template level behavior components like add, remove, and save

- Provides Accordion like behavior that encapsulates either the strictly
header level components `HeaderPanel` (to edit name, description,
and action) or the `Directives` panel to configure the directives
for the template.

- When a switch between panels are required the `{panelConfig: show-header|show-directives}`
is set. When set the render action is triggered and based on current state
the appropriate panel is shown or rendered. See `TemplateHeader:showPanel` method.


### AddTemplateTrigger

This component encapsulates the behavior when the `Add` button is
clicked by the user. The click action is to instantiate the `AddTemplate`
modal dialog.


### AddTemplate Modal

This component encupsulates a modal dialog to capture the
`collection`, `name`, and optionally the `column` attribute
of a template and fire the `App:addTemplate` callback.


### RemoveTemplateTrigger

This component encapsulates the behavior when the `Remove` button is
clicked by the user. The click action will call the `App::removeTemplate`
callback.


### Saving Template Action

This is a simple callback that dispatches to `App:saveTemplate` callaback.

       
<a name="header_panel"/>
## HeaderPanel

Provides for viewing and editing of the following fields. The
class includes the `TextEditMixin` for edit operations.

```
- name
- description
- output
- selected directives
```


<a name="directives"/>
## Directives

The component acts as a container for the drag and drop
directive configuration for a template. It embeds the
directive source and target `LHSList` and `RHSList`
components for drag and drop behavior.


<a name="drag_and_drop"/>
### LHSList (available all directives, DnD source)

The component acts as the `source` of drag drop behavior. It
uses the `Sortable.js` component and provides the `handleSort`
method using the `DirectivesListMixin`.


### RHSList (configured directives, DnD target)

The component acts as the `target` of drag drop behavior. It
uses the `Sortable.js` component and provides the `handleSort`
method using the `DirectivesListMixin`.

The component fires the `App:moveItemWithinList` when items are
moved within the list for re-ordering, and fires the `App:moveItemBetweenList`
when an item is dropped from the `LHSList` component.

Each of the list item embeds `DirectivesEditorTrigger` component
to either edit the configuration or to remove the item from the
template.


<a name="directives_editor"/>
### DirectivesEditorTrigger

Component encapsulates the edit, and remove behavior of included
directives of a template. The remove action triggers a callback
in `App:removeTemplate` method. The `double-click` action triggers
`DirectivesEditor` show modal behavior.


<a name="directives_editor"/>   
### DirectivesEditor

This component simply renders the appropriate modal based on the
`template.directives[i].type` field see `DirectivesEditor:editor`
method. It also provides the `save` callback which collects the
new edits and dispatches to the `App:saveDirective` method.

Some of the modals are below. See `DirectiveEditors` for full list
of editors.

```
- RequireTags
- ReplaceValue
- InsertSubTemplatesFromTagData
...
```

<a name="template_body"/>
## TemplateBody

The component acts a container for the content text area and the
insert bookmark button. The component also provides the
callback to handle the `insert bookmark`.

## ContentEditable

The component simply wraps a `<textarea/>` for the content to be edited. The
onlt special method is `handleInsert`. This method inserts a <tkBookmark/>
tag at the caret position in the text content area, and triggers a state
change.

## InsertBookmarkTrigger

When the the user triggers the `Insert Bookmark` the modal dialog
`InsertBookmark` is activated.

## InsertBookmark (modal)


The modal dialog captures the template `collection`, `name`, and `column` properties
for inserting a sub-template. This component simply delegates the 'save'
action back to `ContentEditable:insertBookmark` method.

<a name="implementation_details"/>
## Implementation Details

<a name="callbacks"/>
### Callbacks

All callbacks pertaining changes to application level state changes, and server
interactions are passed along as component properties. Some of the convention used
are

```
sCB - Save callback
rCB - Remove callback
iCB - Insert callback
mCB - Move item between list callback
aCB - Move item within list callback
dCB - Save directive callback
addTplCB - Add template callback
removeTplCB - REmove template callback
```

<a name="levels_index"/>
### Levels and Index

Due to the recursive/nested nature of the application the following
rules are used to derive the values for component `key` and element id's.

- Level - The current nesting level is passed down through `App` as the `level`
property whenever an `App` component is instantiated.


- Index - When a collection of something is rendered (example ribbon items)
the loop index is passed through as the `index` property of the component.

- Key - When a `key` is required for a component the name of key is suffixed
with the `level` and `index` property of the component.

<a name="jQuery"/>
### jQuery

Some of the components do use `jQuery`. The components are

```
- ContentEditable
- SortableMixin (Directives drag drop)
- Extending/cloning objects
```

<a name="utils"/>
### Utils

The `Utils` class implements

- methods related to extracting the `<tkBookmark/>` tags
- `uuid` generator which is used to suffix sibling accordian panel indices

<a name="screen_shots"/>
## Screen Shots

### Main Controller
- ![Main Controller](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/main-controller.png)

### Template Header
- ![Template Header](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/template-properties-editor.png)

### Add Template Dialog
- ![Add Template](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/add-template.png)

### Bookmark Inserter
- ![Bookmark Inserter](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/bookmark-inserter.png)

### Directives Editor
- ![Directives Editor](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/directives-editor.png)

### Drag Drop Directives
- ![Drag Drop Directives](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/drag-drop-directives.png)

### Nested Template
- ![Nested Template](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/nested-template-editor.png)
