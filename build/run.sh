#!/bin/bash
## This script runs the (built) WAR using the maven tomcat7 plugin
## mvn install needs to be run first on the root

# only run when executed from inside /build dir
if [ -f sample-context.xml ]; then
    set -evx
    #PROJECT_BASE_DIR="$(readlink -f "..")"
    PROJECT_BASE_DIR=$(cd ..; pwd)
    cd ../idmu-war
    mvn tomcat7:run
    cd ../build
else
    echo "Need to run from inside the build dir at <idmu_project_dir>/build : "
    echo "./build.sh"
fi




