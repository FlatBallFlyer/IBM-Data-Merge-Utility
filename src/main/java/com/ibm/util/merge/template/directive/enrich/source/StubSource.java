package com.ibm.util.merge.template.directive.enrich.source;

import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;
import com.ibm.util.merge.template.directive.enrich.provider.StubProvider;

public class StubSource extends AbstractSource {

	public StubSource() {
		super();
		this.setType(AbstractSource.SOURCE_STUB);
	}

	@Override
	public AbstractProvider getProvider() throws MergeException {
		return new StubProvider(this);
	}

	@Override
	public void setOptions(DataObject directive) {
		// TODO Auto-generated method stub
		
	}

}
