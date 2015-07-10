#!/bin/bash

# only run when executed from inside /build dir
if [ -f sample-context.xml ]; then
    set -evx

    ./build.sh
    ./run.sh

else
    echo "Need to run from inside the build dir at <idmu_project_dir>/build : "
    echo "./build.sh"
fi




