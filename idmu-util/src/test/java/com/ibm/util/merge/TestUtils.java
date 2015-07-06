package com.ibm.util.merge;

import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.FilesystemPersistence;

/**
 *
 */
public class TestUtils {
    public static RuntimeContext createDefaultRuntimeContext(){
//        return createRuntimeContext("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates");
        RuntimeContext runtimeContext = createRuntimeContext("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-util/src/test/resources/templates");

        return runtimeContext;
    }

    public static RuntimeContext createRuntimeContext(String jsonTemplatesDirectoryPath) {
        RuntimeContext runtimeContext = new RuntimeContext(new TemplateFactory(new FilesystemPersistence(jsonTemplatesDirectoryPath, new PrettyJsonProxy())));
        return runtimeContext;
    }
}
