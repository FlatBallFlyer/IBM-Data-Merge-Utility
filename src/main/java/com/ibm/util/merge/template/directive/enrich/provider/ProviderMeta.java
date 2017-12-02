package com.ibm.util.merge.template.directive.enrich.provider;

/**
 * A simple wrapper for a Provider Meta-Data that describe how it uses options and commands.
 * 
 * @author flatballflyer
 *
 */
public class ProviderMeta {
	/**
	 * The name to be displayed next to the "Provider Option" value in an Enrich Directive 
	 */
	public String optionName;
	/**
	 * The provider environment memory variable rquriements
	 */
	public String sourceEnv;
	/**
	 * The help to be displayed for provider command
	 */
	public String commandHelp;
	/**
	 * The help to be provided for how this provider handles parsing
	 */
	public String parseHelp;
	/**
	 * The help to be provide for how this format of the object data returned by this provider
	 */
	public String returnHelp;
	
	public ProviderMeta(String optionName, String sourceEnv,
			String commandHelp, String parseHelp, String returnHelp) {
		this.optionName = optionName;
		this.sourceEnv = sourceEnv;
		this.commandHelp = commandHelp;
		this.parseHelp = parseHelp;
		this.returnHelp = returnHelp;
	}

}
