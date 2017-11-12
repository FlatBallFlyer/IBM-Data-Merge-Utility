package com.ibm.util.merge.template.directive.enrich.provider;

public class ProviderMeta {
	public String optionName;
	public String sourceJson;
	public String commandHelp;
	public String parseHelp;
	public String returnHelp;
	
	public ProviderMeta(String optionName, String sourceJson,
			String commandHelp, String parseHelp, String returnHelp) {
		this.optionName = optionName;
		this.sourceJson = sourceJson;
		this.commandHelp = commandHelp;
		this.parseHelp = parseHelp;
		this.returnHelp = returnHelp;
	}

}
