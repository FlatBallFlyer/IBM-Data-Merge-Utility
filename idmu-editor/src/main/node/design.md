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
