package com.awcoleman.DropWizardTestAvroOne.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.dropwizard.auth.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificRecordBase;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.awcoleman.DropWizardTestAvroOne.DWTestAvroOneConfiguration;
import com.awcoleman.DropWizardTestAvroOne.dao.AvroRecordDAO;

/*
 * Return JSON data in format ready for use in jqGrid for
 *   Set of Avro SpecificRecord(s)
 * The JOSN output includes the colNames,ColModel, etc for jqGrid
 *   as well as the data
 * 
 */
@Path("/avrorecforjqgrid")
@Produces(MediaType.APPLICATION_JSON)
public class AvroRecordsForJqGridResource {

	@SuppressWarnings("rawtypes")
	private final static Class avroSRClass = com.awcoleman.examples.DropWizardTestAvroOne.avro.TestRecOne.class;

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(AvroRecordsForJqGridResource.class);

	private final AvroRecordDAO avrorecDao;

	DWTestAvroOneConfiguration c;

	public AvroRecordsForJqGridResource(DBI jdbi, DWTestAvroOneConfiguration ic) {
		avrorecDao = jdbi.onDemand(AvroRecordDAO.class);
		c = ic;
	}

	@GET
	@Path("/{datasetid}")
	public Response getCallDataList(@PathParam("datasetid") String datasetid, @Auth Boolean isAuthenticated) {

		//retrieve the records that match the with the provided dataset id
		Set<SpecificRecordBase> avrorecs = avrorecDao.getDatasetById(datasetid,c.getAvrotablename(),c.getAvrokeycol());

		Response retResp=null;
		if (avrorecs != null ) {
			HashSet<SpecificRecordBase> avrorecswrapper = new HashSet<SpecificRecordBase>();
			avrorecswrapper.addAll(avrorecs);

			Map<String,Object> collForJQGrid = new HashMap<String,Object>();
			
			collForJQGrid.put("datasetid",datasetid);
			collForJQGrid.put("gridData",avrorecswrapper);
			collForJQGrid.put("colNames",avroSRColNamestoJQcolNames(avrorecs.iterator().next().getClass()));
			collForJQGrid.put("colModel",avroSRColNamestoJQcolModel(avrorecs.iterator().next().getClass()));

			retResp = Response.ok(collForJQGrid).build();
		} else {
			//if no records in set, create empty set so JSON output is similar to JSON with records (Could just change this to send "[]").
			HashSet<ArrayList<String>> emptywrapper = new HashSet<ArrayList<String>>();
			ArrayList<String> emptyInner = new ArrayList<String>();
			emptywrapper.add(emptyInner);
			retResp = Response.ok(emptywrapper).build();
		}
		return retResp;
	}
		
	/*
	 * Create List<String> column names for jqGrid colNames
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> avroSRColNamestoJQcolNames(Class avroClass) {
		Schema schema = ReflectData.get().getSchema(avroClass);
		List<Schema.Field> schemaFields = schema.getFields();

		List<String> schemaFieldNames = new ArrayList<String>();
		for (Schema.Field thisField : schemaFields) {
			schemaFieldNames.add(thisField.name());
		}
		
		return schemaFieldNames;
	}
	
	/*
	 * Create List<Map<String,Object>> of column names and some parameters for jqGrid colModel
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map<String,Object>> avroSRColNamestoJQcolModel(Class avroClass) {

		Schema schema = ReflectData.get().getSchema(avroClass);
		List<Schema.Field> schemaFields = schema.getFields();

		List<Map<String,Object>> schemaColModel = new ArrayList<Map<String,Object>>();

		for (Schema.Field thisField : schemaFields) {			
			Map<String,Object> params = new HashMap<String,Object>();
			
			params.put("name", thisField.name());
			params.put("sortable", true);

			schemaColModel.add(params);
		}
		
		return schemaColModel;
	}
}
