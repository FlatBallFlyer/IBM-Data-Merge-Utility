# Table of Contents

- [Overview](#overview)
- [Component BOM](#bom)

<a name="overview"/>
# Overview

This document presents the main components in the application
and also documents the individual components.

<a name="bom"/>
# Component BOM
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

## App

The App component encapsulates the top top-level state, and all
interactions with the server. It is also the primary source of
render trigger due to state changes. The component is meant to
be embedded inside other components in a nested fashion.

The important top-level states are

```
- selected collection
- selected ribbon item
- global directives
- templates for selected collection
- current template in view
```

The important server interaction methods are

```

- loadDirectives
- loadCollections
- loadTemplates
- loadTemplate
- addTemplate
- removeTemplate
- saveTemplate

```

## TemplateCollection

The class encapsulates the selection behavior of top-level templates.
Note that this component is never utilized when the `App` component
is nested. This logic is implemented in the `App:header` method.


## TemplateRibbon

The ribbon component acts as a container for a group of templates in
a selected collection. The behavior is like a cover-flow showing a
single template at a time. Provisions for left/right navigation are
provided.

For each concrete template in the collection a `TemplateRibbonItem`
is embedded. The template itself is lazily loaded when the item
is in view.

When the left/right buttons are clicked the `App:collectionSelected`
is called.


## TemplateRibbonItem

The class encapsulates ribbon related behavior and embeds the
`TemplateEditor` component for a particular template.

## TemplateEditor

The class acts as container for multiple components as stated
in the BOM. The class also acts as a narrow channel for template
save actions.

### Handling Of Bookmarks

When it is time to render this component the following
action takes place.

- The `TemplateHeader` is instantiated and rendered.

- Next `template.content` is chunked into `body_items`. The chunking happens
at the boundries of detected bookmarks `<tkBookmark/>` tags.

- Each item in the collection can either be of type `text` or of
type `bookmark` with associated data attribute (i.e. text or tag with
attributes)

- Finally the `body_items` are traversed and based on the type of
the item either a `TemplateBody` is instantiated or an `App` is
instantiated.


### Handling Of Save Action

When a save action happens either due to user pressing the `Save` button
or when a bookmark is inserted the `TemplateEditor:handleSave` method is
called. The following sequence of action happens.

- The header values are collected

- The configured directives are collected

- The `template.content` text is re-constructed using newly introduced
text content and/or any newly inserted bookmark.

- Finally the `App:saveTemplate` method is invoked.


## TemplateHeader

The header component provides for the following.

- Embed template level behavior components like add, remove, and save

- Accordion like behavior that encapsulates either the strictly
header level components `HeaderPanel` (to edit name, description,
and action) or the `Directives` panel to configure the directives
for the template.


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

       
## HeaderPanel

Provides for viewing and editing of the following fields. The
class includes the `TextEditMixin` for edit operations.

```
- name
- description
- output
- selected directives
```


## Directives


### LHSList (available all directives, DnD source)


### RHSList (configured directives, DnD target)
          [<li>DirectivesEditorTrigger</li> ...]

### DirectivesEditorTrigger

   Actions [edit, remove]
   
### DirectiveEditors (modal)
           RequireTags
           ReplaceValue
           InsertSubTemplatesFromTagData




