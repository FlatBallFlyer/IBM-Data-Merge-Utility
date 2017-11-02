package com.ibm.util.merge.template.directive.enrich.provider;

import java.util.HashMap;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;

public interface ProviderInterface {

	public abstract DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws MergeException;
	public abstract String getSource();
	public abstract String getDbName();
	public abstract Merger getContext();

}
