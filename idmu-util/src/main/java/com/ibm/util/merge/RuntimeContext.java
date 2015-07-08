package com.ibm.util.merge;

import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 *
 */
public class RuntimeContext {
    private static final Logger log = Logger.getLogger(RuntimeContext.class);
    private final TemplateFactory templateFactory;
    private final ConnectionFactory connectionFactory;
    private Date initialized = null;

    public RuntimeContext(TemplateFactory templateFactory) {
        this.templateFactory = templateFactory;
        connectionFactory = new ConnectionFactory();
        log.info("Instantiated");
    }

    public void initialize(String outputDirPath) {
        log.info("Initializing..");
        Path path = Paths.get(outputDirPath);
        if (!Files.exists(path)) {
            throw new OutputDirectoryPathDoesNotExistException(outputDirPath);
        }
        if (!Files.isDirectory(path)) {
            throw new NonDirectoryAtOutputDirectoryPathException(outputDirPath);
        }
//        zipFactory.setOutputRoot(outputDirPath);
        templateFactory.reset();
        templateFactory.loadTemplatesFromFilesystem();
        initialized = new Date();
        log.info("Initialized");
    }

    public Date getInitialized() {
        return initialized;
    }

    public TemplateFactory getTemplateFactory() {
        return templateFactory;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }


    public static class OutputDirectoryPathDoesNotExistException extends RuntimeException {
        private String outputDirPath;

        public OutputDirectoryPathDoesNotExistException(String outputDirPath) {
            super("The output directory does not exist: " + outputDirPath);
            this.outputDirPath = outputDirPath;
        }

        public String getOutputDirPath() {
            return outputDirPath;
        }
    }

    public static class NonDirectoryAtOutputDirectoryPathException extends RuntimeException {
        private String outputDirPath;

        public NonDirectoryAtOutputDirectoryPathException(String outputDirPath) {
            super("Path does not denote a directory: " + outputDirPath);
            this.outputDirPath = outputDirPath;
        }

        public String getOutputDirPath() {
            return outputDirPath;
        }
    }
}
