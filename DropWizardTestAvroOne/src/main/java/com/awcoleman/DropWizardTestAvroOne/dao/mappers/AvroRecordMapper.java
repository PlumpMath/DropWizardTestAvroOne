package com.awcoleman.DropWizardTestAvroOne.dao.mappers;

import org.skife.jdbi.v2.BeanMapper;
import com.awcoleman.examples.DropWizardTestAvroOne.avro.TestRecOne;

/*
 * Make sure the DropWizard database configuration for Hive has:
 * initializationQuery: SET hive.resultset.use.unique.column.names=false
 * 
 * otherwise the column names returned are tablename.columnname (such
 * as "testrecone.datasetid" and BeanMapper will not match fields.
 * 
 * See: https://issues.apache.org/jira/browse/HIVE-8889
 * 
 */
public class AvroRecordMapper extends BeanMapper<TestRecOne> {

	public AvroRecordMapper() {
		super(TestRecOne.class);
	}

}
