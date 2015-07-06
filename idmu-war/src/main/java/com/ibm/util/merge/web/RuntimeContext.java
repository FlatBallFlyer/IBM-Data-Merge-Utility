package com.ibm.util.merge.web;

import com.ibm.util.merge.ConnectionFactory;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.ZipFactory;
import org.apache.log4j.Logger;

/**
 *
 */
public class RuntimeContext {
    private static final Logger log = Logger.getLogger(RuntimeContext.class);
    private final TemplateFactory templateFactory;
    private final ZipFactory zipFactory;
    private ConnectionFactory connectionFactory;

    public RuntimeContext(TemplateFactory templateFactory, ZipFactory zipFactory) {
        this.templateFactory = templateFactory;
        this.zipFactory = zipFactory;
        this.connectionFactory = new ConnectionFactory();
        log.info("Instantiated");
    }



    public void initialize(String outputDirPath) {
        log.info("Initializing..");
        zipFactory.setOutputroot(outputDirPath);
        templateFactory.loadTemplatesFromFilesystem();
        log.info("Initialized");
    }

    public TemplateFactory getTemplateFactory() {
        return templateFactory;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public ZipFactory getZipFactory() {
        return zipFactory;
    }
}
