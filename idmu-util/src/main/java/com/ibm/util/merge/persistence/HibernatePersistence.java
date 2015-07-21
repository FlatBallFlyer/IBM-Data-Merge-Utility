package com.ibm.util.merge.persistence;

import com.ibm.util.merge.template.Template;

import org.apache.log4j.Logger;
import java.util.List;

/**
 *
 */
public class HibernatePersistence extends AbstractPersistence {
    private Logger log = Logger.getLogger(HibernatePersistence.class);

    public HibernatePersistence() {
    	log.info("Instantiated");
    }
    
    @Override
	public List<Template> loadAllTemplates() {
		// templateCache.clear();
		// TODO Load all templates from database
        throw new UnsupportedOperationException("implement load all templates from DB");
	}


	@Override
	public void saveTemplate(Template template) {
		// TODO Delete an existing template and save the new one
		deleteTemplate(template);
        throw new UnsupportedOperationException("implement save template from DB");
	}

	@Override
	public void deleteTemplate(Template template) {
		// TODO Delete the template if it exists, log errors, do not throw errors
        throw new UnsupportedOperationException("implement delete in DB");
    }
    

}
