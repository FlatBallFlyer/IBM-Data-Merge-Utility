package com.ibm.util.merge.persistence;

import com.ibm.util.merge.template.Template;

import java.util.List;

public abstract class AbstractPersistence {

	public AbstractPersistence() {
		
	}
	
	public abstract List<Template> loadAllTemplates();
	public abstract void saveTemplate(Template template);
	public abstract void deleteTemplate(Template template);
}
