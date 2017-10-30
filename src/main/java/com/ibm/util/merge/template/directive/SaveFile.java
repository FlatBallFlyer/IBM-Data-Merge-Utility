package com.ibm.util.merge.template.directive;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.storage.Archive;

public class SaveFile extends AbstractDirective {
	String filename;
	Boolean clearAfter;
	
	public SaveFile() {
		super();
		this.setType(AbstractDirective.TYPE_SAVE_FILE);
		this.clearAfter = false;
	}

	@Override
	public void execute(Merger context) throws MergeException {
		Archive archive = context.getArchive();
		archive.writeFile(this.filename, this.template.getContent(), "idmu-user", "idmu-group");
		if (this.clearAfter) {
			this.template.clearContent();
		}
	}

	@Override
	public AbstractDirective getMergable() {
		SaveFile mergable = new SaveFile();
		this.makeMergable(mergable);
		mergable.setFilename(filename);
		mergable.setClearAfter(clearAfter);
		return mergable;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Boolean getClearAfter() {
		return clearAfter;
	}

	public void setClearAfter(Boolean clearAfter) {
		this.clearAfter = clearAfter;
	}

}
