package com.ibm.util.merge;

import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.FilesystemPersistence;

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
        ConnectionPoolManager poolManager = new ConnectionPoolManager();
        MergeContext mergeContext = new MergeContext(new TemplateFactory(new FilesystemPersistence(jsonTemplatesDirectoryPath, new PrettyJsonProxy(), outputRoot)), poolManager);
        return mergeContext;
    }
}
