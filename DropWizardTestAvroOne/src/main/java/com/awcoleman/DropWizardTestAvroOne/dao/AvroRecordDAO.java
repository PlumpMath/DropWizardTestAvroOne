package com.awcoleman.DropWizardTestAvroOne.dao;

import java.util.Set;

import org.apache.avro.specific.SpecificRecordBase;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.awcoleman.DropWizardTestAvroOne.dao.mappers.AvroRecordMapper;

/*
 * Make sure the DropWizard database configuration for Hive has:
 * initializationQuery: SET hive.resultset.use.unique.column.names=false
 * 
 * otherwise the column names returned are tablename.columnname (such
 * as "testrecone.datasetid" and BeanMapper will not match fields.
 * 
 * See: https://issues.apache.org/jira/browse/HIVE-8889
 */
@UseStringTemplate3StatementLocator
public interface AvroRecordDAO {

	//table is the Hive table created from the Avro directory, keycol is the key column. Both should be defined in the config file.
	@Mapper(AvroRecordMapper.class)
	@SqlQuery("select * from <table> where <keycol>= :datasetid")
	Set<SpecificRecordBase> getDatasetById(@Bind("datasetid") String datasetid, @Define("table") String table, @Define("keycol") String keycol);
	
/*
 * 	An example without table/keycol names from the config file would be:
 * (remove class UseStringTemplate3StatementLocator annotation)
 * 
 *	public final static String avroTablename = "TestRecOne";
 *	public final static String avroDatasetIDColName = "datasetID";
 *	
 *	//avroTablename is the Hive table created from the Avro directory, avroDatasetIDColName is the key column.
 *	@Mapper(AvroRecordMapper.class)
 *	@SqlQuery("select * from "+avroTablename+" where "+avroDatasetIDColName+"= :datasetid")
 *	Set<SpecificRecordBase> getDatasetById(@Bind("datasetid") String datasetid);
 *
*/
}
