# Requirements for Template Editor

UI Design has several re-used components:

- Basic Template Editor: edits a single Template
- Template Ribbon: A horizontally scrolling collection of 'Template Editors'

## Basic Template Editor
Is composed of

### 1.A 'Title' bar with a twistee for extra data.
The contents of the Title Bar vary between the top-level and sub-templates

#### a.Top Level Editor Title Bar

- Collection - Pick List of all Collection Values
The user can add a new value that is not listed
A change in this value re-populates the top-level Template Ribbon

- Name: text input

- Directives: Pull Down list


#### b.Sub-Template Title Bar

- Column Value: (Only if Column Name is in bookmark)

- Directives: Pull-down list


#### c.Additional Fields exposed by twistee

- Description -

- Output File Name

- Directives - An editable (add/remove), orderable list of 'Directives'

### 2.The 'Template Textarea' - which fills most of the available canvas, and grows/shrinks vertically to the size of the contents. Word wrapping is not desirable.

### 3.A button to 'Insert Bookmark' - only visible if the template has InsertSubs directives.

- This button will prompt for a 'Bookmark Name' 'Collection' and 'Column Name' values. After these values are provided, you will populate a template ribbon of templates with the specified name from the specified collection. This Template Ribbon will be inserted, with a new text area just below it.

- This new Textarea is a continuation of template content.

- Actual template content will contain <tkBookmark elements with Name, Collection and Column Value.

- If a column value is provided, the template menu bar area will include a 'Column Value' editable field, otherwise only Description and Directives are editable. Name and Collection are displayed as read-only.

- This bookmark is what is represented by the Sub-Template Ribbon in the UI.

- If a block of text in the template is selected when the button is pressed, that text will be removed and inserted in the sub-template for editing.

## Q&A

### Clarification

- A template starts at a single root template and expands/fans out like a tree with sub-templates.
Yes, but I would avoid the "Tree" thinking as it's more of a side-effect than an actual design feature. Think of a template as a Document with "Bookmarks" where sub-templates are inserted.

- A template editor is to edit a template starting at the root template (effective) or from any of sub-templates.
Yes, the important part is to be able to edit sub-templates "in-line" showing a sub-template instead of a "bookmark".

- The 'Template Ribbon' is a way to navigate/switch between the effective root template and all it's sub templates.
Yes for the "Top" level editor. We start with the "root" collection and allow the user to select other collections as a base starting point.
Sub-template ribbons are used when the sub-template to be inserted will be varied based on the data (Column) that is being processed.

- A collection is a named list of key/value pairs whose scope starts at the effective root template. Collection names can shadow (override) the ones that are inherited from the outer scope.

- A template has a "Full Name" that consists of the Collection Name, the Template Name and a "Column Value"
In situations where we are not inserting sub-templates based on a column value, the Collection is just a way to organize templates into groups. When we are varying the sub-template inserted based on data being processed the Collection has real significance.

### Questions

- section 1. Twistee for extra data .. You mean a toggle to expand/collapse extra data/view details right ?
Yes, I was thinking of something along these lines: http://jqueryui.com/resources/demos/accordion/collapsible.html

- section 1.a Why should the top-level template ribbon repopulate itself if the collection pick-list is changed ?
The top-level collection pick-list gives the user a way to determine the top-level template collection they are editing.

- section 1.b The editable list of ordered directives .. I thought that there was a single set of built-in directives .. Can new directives be added on the fly ? If so where and how is this done ?
Yes, the editable, ordered list of Directives can grow or shrink. There is a fixed number of Directive Types, but multiple directives, even of the same type, can be associated with a template. The Directives will need to support Add/Remove/Edit/Reorder functionality. I was thinking something like this: http://jqueryui.com/resources/demos/sortable/connect-lists.html with the addition of a "New" and "Delete" button, and a double-click to open an "Directive Editor" dialog. The "Editor" dialog will be slightly different for each directive type, but they are all different combinations of a set of "Directive" and "Provider" data. If you review the Documentation.html sections on Directives you will see the commonality.

- section 3.e "... by the Sub-Template Ribbon .." you mean the general top level "Ribbon" right ?
No, when you insert a sub-template (bookmark) into a template, the user is prompted for a "Collection Name, Template Name and Column Name" value. If the "Column Name" value is blank, there is no ribbon, just a single sub-template. If the "Column Name" is non-blank, the ribbon will contain all of the templates where the "Collection" and "Name" values match the bookmark. See the "Inserting Sub-Templates" topic in the Documentation.html page for more details, you need to understand bookmarks and sub-templates. It is not mentioned in the design doc, but a "New Template" button is needed for sub-templates that use a "Column Name", or at the Top Level ribbon. The "New Template" dialog should prompt for Collection, Template and Column Value on the "Top" level ribbon, on for only "Column Value" on sub-templates.

- section 4. What is the dot notation for template names (e.g. Root.default.) can you explain this a bit?
A template full name is made up of 3 parts, the collection name, template name and column value concatenated with "." - hopefully #3 above helps to explain this.

- How does the user get hold of all available templates ? I suppose from menu/auto-complete function.
This is the function of the top-level collection drop-down list. The page should start with the "root" collection in the main ribbon. The user can then select a different collection if needed to load a different set of top level templates.



## Other Notes

JSON Formats are in Documentation.html of the github v3 branch

Simplest Possible Display - Top Level
Template Data Exposed by twistee

Insert SubTemplate button visible if Directives contains 'Insert'

After the Insert Subs button is pressed and dialog closed

## Sub Template Notes
- Button below content area (if insert subtamplates) in the directives for template
- when button clicked prompt for collection, template, and column name
- split the text area into two with the template in between
- the in-between template will have the collection, template, column name
  must be able to provide the column value

- Need directive type name
- Need add/delete buttons
- DnD to be inside the puldown are
- so we just have one modal fore the drtvs

