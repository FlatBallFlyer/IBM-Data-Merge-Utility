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
package com.ibm.util.merge.directive;
import java.sql.ResultSet;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Merge;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.ProviderSql;

/**
 * <p>This class represents a replaceColumn directive which loads the Replace hashmap
 * with the values of a "from" and "to" column in a sql result set.</p>
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Merge
 */
public class ReplaceColSql extends ReplaceCol implements Cloneable {
	
	/**
	 * @param dbRow
	 * @param owner
	 * @throws MergeException
	 */
	public ReplaceColSql(ResultSet dbRow, Template owner) throws MergeException {
		super(dbRow, owner);
		this.provider = new ProviderSql(this, dbRow);
	}

	/** 
	 * Simple Clone constructor
	 * @see com.ibm.util.merge.directive.InsertSubs#clone()
	 */
	public ReplaceColSql clone(Template owner) throws CloneNotSupportedException {
		return (ReplaceColSql) super.clone(owner);
	}
}
