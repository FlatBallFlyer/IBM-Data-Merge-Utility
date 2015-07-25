/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
