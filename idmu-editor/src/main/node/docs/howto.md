# Table of Contents

- [Load A Collection](#load_collection)
- [Navigate Between Templates](#navigate_templates)
- [Add Template](#add_template)
- [Remove Template](#remove_template)
- [Save Template](#save_template)
- [Edit Template Properties](#edit_template_properties)
- [Configure Template Directives](#configure_template_direcives)
- [Edit Template Directives](#edit_template_directive_properties)
- [Insert Sub-Template](#insert_sub_template)


<a name="load_collection"/>
## Load A Collection

- Load the application in browser
- Select a collection from the 'Collections' select list
- A soon as the collection is selected all templates are loaded in the collection
- You may navigate between template by clicking on the Left/Right bars of the template editor

![Load A Collection](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/main-controller.png)


<a name="navigate_templates"/>
## Navigate Between Templates

After a collection is selected, templates in the collection can be viewed by
clicking on the Left/Right bar of the template view.

![Template Navigation](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/left-right-nav.png)

<a name="add_template"/>
## Add Template

To add a template click on the 'Add' button available on the title bar of a template. A dialog
box will be presented to get the collection, name of template, and columnValue if any. By clicking
on the 'Add' button on the dialog a new template will be created according to the spec.

![Add Template](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/add-template.png)

<a name="remove_template"/>
## Remove Template

To remove a template load the templates from the collection, navigate to the
template of interest and press the 'Remove' button on the title bar of the
template. 

![Remove Template](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/left-right-nav.png)

<a name="save_template"/>
## Save Template

To save a template load the templates from the collection, navigate to the
template of interest, make changes and press the 'Save' button on the title
bar of the template. 

![Save Template](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/left-right-nav.png)

<a name="edit_template_properties"/>
## Edit Template Properties

The basic template properties like name, output, and description 
can be edited or changed by clicking on the title of the
template (which is shown as a hyper-link). This will expand the area and show the
basic properties of the template. After making changes you may wish to save the
template.

![Edit Template Properties](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/template-properties-editor.png)

<a name="configure_template_directives"/>
## Configure Template Directives

To configure the template directives

- Expand the template properties area by clicking on the title of the template
- Click on the 'Configure' button next to the directives select list
- A new panel is shown as in the image below.
- You can drag the items from the left hand side list onto the right and side. This
  action will add the selected directive to the template.
- You may also rearrange the order of directives in the template by dragging the left
 hand side items and placing it in the right position.
- To remove a directive from the template click on the 'red' cross button on the item.


![Configure Directives ](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/drag-drop-directives.png)


*Note* - After adding or changing directives and/or it's properties you must hit the 'Back'
button on this panel, and then press the 'Save' button on the title bar. Otherwise your
changes will not be saved.

<a name="edit_template_directive_properties"/>
## Edit Template Directives

To change the properties of a configured template directive

- go to the directive configuration panel
- double-click on the item on the left hand side of the list.
- This will bring up a property editor for that particular directive type.
- Make your edits and press the 'Save' button on the dialog editor
- Make sure to hit the 'Back' button
- Finally 'Save' the whole template

![Edit Directives](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/directives-editor.png)

<a name="insert_sub_template"/>
## Insert Sub-Template

- To insert a sub-template you must have configured the template
with an 'Insert' directive.

- If there is an 'Insert' directive a 'Insert Template' button will appear below
the 'Content' area.

- By clicking on the 'Insert Template' button a dialog is presented to capture the
collection, template name, and column name (optional) for the sub-template.

- When the 'Insert' button is pressed a new bookmark is inserted into the content and
the template screen is refreshed with the new bookmark expanded as a sub-template.

![Insert Button](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/insert-template-button.png)


![Insert Dialog](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/bookmark-inserter.png)


