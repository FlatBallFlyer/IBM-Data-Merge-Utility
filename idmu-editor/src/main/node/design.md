# Overview

This document presents the main components in the application
and also documents the individual components.

# Component BOM
```
App
  TemplateCollection
  Ribbon
   RibbonItem
    TemplateEditor
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