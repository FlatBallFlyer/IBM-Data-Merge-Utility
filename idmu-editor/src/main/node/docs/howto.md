# Table of Contents

- [Load A Collection](#load_collection)
- [Add Template](#add_template)
- [Remove Template](#remove_template)
- [Save Template](#save_template)
- [Edit Template Properties](#edit_template_properties)
- [Hashes and Template Directives] (#hashes_directives)
- [Configure Template Directives](#configure_template_direcives)
- [Edit Template Directives](#edit_template_directive_properties)
- [Insert Sub-Template](#insert_sub_template)


<a name="load_collection"/>
## Load A Collection

A collection is a container for organizing templates. You can create multiple containers that have one or many templates assigned to them.

To access collections, perform the following steps:

1. Navigate to the IDMU application in your browser.
2. Select a collection from the 'Collections' select list.
3. Upon selection of a collection, all the IDMU will display all the available templates in the collection.
4. You may navigate between the templates by clicking on the Left/Right bars in the template editor.
 
Figure #1 - Collection Page

![Load A Collection](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/main-controller.png)

Figure #2 Navigating Between Templates Using Left and Right Arrows

![Template Navigation](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/left-right-nav.png)

<a name="add_template"/>
## Add Template

The template is the core component of the IDMU Template editor. A template provides the ability to create a message, define merge fields, specify directives to drive the retrevial of data, and perform the required inserts of that data. Once the IDMU processes the template, the IDMU can deliver multiple output files from a single merge into a zip or tar archive.

Perform the following steps to create a template:

1. Click on the 'Add' button in the title bar of a template. 
2. Upon clicking, the IDMU will launch a dialog box will enabling you to enter the collection, name of template, and columnValue if any.
3. Upon completion of these fields, click on the 'Add' button on the dialog. This will create the new template.

Figure 3 - Creating a new template

![Add Template](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/add-template.png)

<a name="remove_template"/>
## Removing a Template

You can add or remove templates in any collection. Perform the following steps to remove a template:

1. Navigate to the collection and template of interest within that collection.
2. Click on the 'Remove' button on the top right of the title bar of the template.

Figure 4 - Removing a template

![Remove Template](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/left-right-nav.png)

<a name="save_template"/>
## Saving a Template

You can save a template in any collection. Perform the following steps to save a template:

1. Navigate to the collection and template of interest within that collection.
2. Click on the 'Save' button on the top right of the title bar of the template.

Figure 5 - Saving a template

![Save Template](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/left-right-nav.png)

<a name="edit_template_properties"/>
## Editing Template Properties

You can edit the basic template properties like name, output, and description.  Perform the following steps to edit template properties: 

1. Click on the title of the template from within the collection.
2. The template  expand the area and show the basic properties of the template. 
3. After making changes, click on the "Save" button of the template.

Figure 6 - Edit template properties

![Edit Template Properties](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/template-properties-editor.png)

<a name="hashes_directives"/>
## Hashes and Directives

The most fundamental aspect of merge processing is the Replace Hash. The Replace Hash specifies that its contents will be replaced by the values retreived from a merge directive. A merge directive specifies the data providers and data values to be inserted into a hash. Upon execution, the IDMU will execute all merge directives in a template, extract the data from the proper sources and execute a "find and replace" for all instances of the Replace Hash.

The two most common merge directives available in an IDMU template are: Replace Row and Replace Column. T

* Replace Row - This directive expects the data provider to return a single row of data. Upon return, the IDMU places the column names in brackets and places the "From" value in the Replace Hash, with the data being placed in the "To" value.

* Replace Column - This directive expects a multi-row result set and uses the columns that you specify as From and To values. Similar to "Replace Row", Replace Colum wraps the "From" value in brackets. Notice that a replace value that already exists in the hash is updated with new data from the same key. 

<a name="configure_template_directives"/>
## Configure Template Directives

The IDMU template editor provides multiple merge directives. For a list of available merge directives, click here [link needed]. Once you have identified the merge directive(s) you want to use, perform the following steps:

1. Locate the template you wish to edit.
2. Click on the title of the template, expanding it.
3. Click on the 'Configure' button next to the directives "Select" list. Upon clicking, the IDMU launches the drag and drop directives panel.
4. You can drag the merge directives from the left hand side list onto the right and side. This action will add the selected directive to the template.
5. In addition to the drag and drop of the directives, you can also rearrage the order of the directives.
6. To remove a directive from the template click on the 'red' X button to the right of the merge directive.

*Note* - After adding or changing directives and/or it's properties you must hit the 'Back'
button on this panel, and then press the 'Save' button on the title bar. Otherwise your
changes will not be saved.

Figure 6 - Dragging merge directives

![Configure Directives](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/drag-drop-directives.png)

<a name="edit_template_directive_properties"/>
## Editing Template Directives

Once you have selected the merge directives for your template, you can edit them to configure the specific data provider information. 

To change the properties of a configured template directive, perform the following:

1. Navigate to the directive configuration panel.
2. Click on the directive on the lefthand side of the drag and drop list.
3. Upon clicking, the IDMY will launch the property editor for that particular directive type.
4. Make your edits (for example, update a "WHERE" clause) and press the 'Save' button on the dialog editor.
5. As noted earlier, click the 'Back' button.
6. To complete the edit, click on the "Save" button for the template.

Figure 7 - Editing merge directives  

![Edit Directives](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/directives-editor.png)

<a name="insert_sub_template"/>
## Insert Sub-Template

The IDMU allows you to insert sub-templates into templates. The "Insert Subs" directive works with a "Bookmark" to create, process, and insert a sub-template. The IDMU inserts one sub-template for each row of data returned by the data provider specified in the "Insert Subs" directive. From there, the sub-template inherits the full Replace Hash of its parent template, as well as the replace row values of the current record that is driving the insert. Sub-templates can additionally have their own directives to drive additional processing within their own template.

To insert a sub-template you must have configured the template, perform the following:

1. Ensure you have dragged an 'Insert' directive to the left of the template. You will know the "Insert" directive is available because you will see  an 'Insert Template' button will appear below the 'Content' area.
2. After clicking on the 'Insert Template' button, the IDMU will present a dialog to create the collection, template name, and column name (optional) for the sub-template.
3. Additionally, when you click on the 'Insert' button, a the IDMU inserts a new bookmark into the content. The IDMU will then refresh the template screen with the new bookmark, expanding it as a sub-template.

Figure 8 - Creating the sub-template

![Insert Button](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/insert-template-button.png)

Figure 9 - Creating the bookmark

![Insert Dialog](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/post-integ/idmu-editor/src/main/node/docs/screen-shots/bookmark-inserter.png)


