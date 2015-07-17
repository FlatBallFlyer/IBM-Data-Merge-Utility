#!/bin/bash

# only run when executed from inside /build dir
if [ -f sample-context.xml ]; then
    set -evx

    PROJECT_BASE_DIR=$(cd ..; pwd)
    # clean generated static sources
    rm -rf "$PROJECT_BASE_DIR"/idmu-editor/src/main/resources/META-INF/resources/editor/*

    # enter static sources project root
    cd "$PROJECT_BASE_DIR"/idmu-editor/src/main/node
    # remove the build dir
    rm -rf build

    # build the static sources
    npm install
    bower install
    gulp fonts-copy-init
    gulp



    # copy generated static sources to the path to have them included as part of the webapp at /editor
    mkdir -p "$PROJECT_BASE_DIR/idmu-editor/src/main/resources/META-INF/resources/editor"
    cp -R "$PROJECT_BASE_DIR"/idmu-editor/src/main/node/build/* "$PROJECT_BASE_DIR"/idmu-editor/src/main/resources/META-INF/resources/editor/
    #cp -R "$PROJECT_BASE_DIR"/idmu-editor/src/main/node/bower_components "$PROJECT_BASE_DIR"/idmu-editor/src/main/resources/META-INF/resources/editor/
    #cp -R "$PROJECT_BASE_DIR"/idmu-editor/src/main/node/src "$PROJECT_BASE_DIR"/idmu-editor/src/main/resources/META-INF/resources/editor/

    # build maven ignoring tests
    cd "$PROJECT_BASE_DIR"
    mvn -DskipTests clean install
	cd "$PROJECT_BASE_DIR"/build
else
    echo "Need to run from inside the build dir at <idmu_project_dir>/build : "
    echo "./build.sh"
fi




