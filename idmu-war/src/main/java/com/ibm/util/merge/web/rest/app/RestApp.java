package com.ibm.util.merge.web.rest.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;



@ApplicationPath("/rest")
public class RestApp extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(CollectionsResource.class, CollectionResource.class, TemplatesResource.class, TemplateResource.class));
    }
}
