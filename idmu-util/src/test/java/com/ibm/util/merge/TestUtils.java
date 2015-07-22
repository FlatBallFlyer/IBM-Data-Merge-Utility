package com.ibm.util.merge;

import java.io.File;

/**
 *
 */
public class TestUtils {
    public static MergeContext createDefaultRuntimeContext(){
//        return createRuntimeContext("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates");
        File jsonTemplatesDirectoryPath = new File("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-util/src/test/resources/templates");
        File outputRoot = new File("/tmp/merge");
        MergeContext mergeContext = createRuntimeContext(jsonTemplatesDirectoryPath, outputRoot);

        return mergeContext;
    }

    public static MergeContext createRuntimeContext(File outputRoot){
        File jsonTemplatesDirectoryPath = new File("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-util/src/test/resources/templates");

        MergeContext mergeContext = createRuntimeContext(jsonTemplatesDirectoryPath, outputRoot);

        return mergeContext;
    }

    public static MergeContext createRuntimeContext(File jsonTemplatesDirectoryPath, File outputRoot) {
        MergeContext mergeContext = null;
        return mergeContext;
    }
}
