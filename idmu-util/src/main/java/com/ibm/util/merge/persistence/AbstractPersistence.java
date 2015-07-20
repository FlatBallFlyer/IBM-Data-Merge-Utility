package com.ibm.util.merge.persistence;

import java.util.List;

import com.ibm.util.merge.template.Template;

public abstract class AbstractPersistence {

	public AbstractPersistence() {
		
	}
	
	public abstract List<Template> loadAllTemplates();
	public abstract void saveTemplate(Template template);
	public abstract void deleteTemplate(Template template);
}
