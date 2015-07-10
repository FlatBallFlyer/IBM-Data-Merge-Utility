package com.ibm.util.merge;

import com.ibm.util.merge.template.Template;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;

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

    public void initialize() {
        log.info("Initializing..");
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

    /**
     * @param error
     * @return
     */
    public String getHtmlErrorMessage(MergeException error) {
        String message;
        Template errorTemplate;
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Template.wrap("MESSAGE"), error.getError());
        parameters.put(Template.wrap("CONTEXT"), error.getContext());
        parameters.put(Template.wrap("TRACE"), error.getStackTrace().toString());
        try {
            errorTemplate = getTemplateFactory().getTemplate("system.errHtml." + error.getErrorFromClass(), "system.errHtml.", parameters);
            errorTemplate.merge(this);
            final String returnValue;
            if (!errorTemplate.canWrite()) {
                returnValue = "";
            } else {
                returnValue = errorTemplate.getContent();
            }
            getTemplateFactory().getFs().doWrite(errorTemplate);
            message = returnValue;
        } catch (MergeException e) {
            message = "INVALID ERROR TEMPLATE! \n" +
                    "Message: " + error.getError() + "\n" +
                    "Context: " + error.getContext() + "\n";
        }
        return message;
    }

    /**
     * @param error
     * @param throwable
     * @return
     */
    public String getJsonErrorMessage(MergeException error) {
        String message;
        Template errorTemplate;
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Template.wrap("MESSAGE"), error.getError());
        parameters.put(Template.wrap("CONTEXT"), error.getContext());
        parameters.put(Template.wrap("TRACE"), error.getStackTrace().toString());
        try {
            errorTemplate = getTemplateFactory().getTemplate("system.errJson." + error.getErrorFromClass(), "system.errJson.", parameters);
            errorTemplate.merge(this);
            final String returnValue;
            if (!errorTemplate.canWrite()) {
                returnValue = "";
            } else {
                returnValue = errorTemplate.getContent();
            }
            getTemplateFactory().getFs().doWrite(errorTemplate);
//			errorTemplate.doWrite(rtc.getZipFactory());
            message = returnValue;
//			errorTemplate.packageOutput(zf, cf);
        } catch (MergeException e) {
            message = "INVALID ERROR TEMPLATE! \n" +
                    "Message: " + error.getError() + "\n" +
                    "Context: " + error.getContext() + "\n";
        }
        return message;
    }
}
