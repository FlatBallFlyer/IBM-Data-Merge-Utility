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
