package com.awcoleman.DropWizardTestAvroOne.resources;

import java.util.ArrayList;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

			//Use temporary hack method to create String holding JSON representation of Set of Avro SpecificRecords
			String griddatajson = AvroRecordsResource.specificRecordSetToJSONarray(avrorecswrapper,avroSRClass);

			
			String colnamejson=avroSRColNamestoJQcolNames(avroSRClass);

			String colmodeljson=avroSRColNamestoJQcolModel(avroSRClass);
			
			String jqresp=concatStringsForJqGrid(datasetid,colnamejson,colmodeljson,griddatajson);

			retResp = Response.ok(jqresp, MediaType.APPLICATION_JSON).build();
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
	 * Hack to combine jqGrid component JSON strings into a JSON object to send to jqGrid
	 * 
	 */
	private String concatStringsForJqGrid(String datasetid, String colnamejson, String colmodeljson, String griddatajson) {
		String jqgridresp = null;
		StringBuilder jqbldr = new StringBuilder();
		
		jqbldr.append("{");
		jqbldr.append("\"datasetid\" :");
		jqbldr.append("\""+datasetid+"\",");

		jqbldr.append("\"colNames\" :");
		jqbldr.append(colnamejson);
		jqbldr.append(",");

		jqbldr.append("\"colModel\" :");
		jqbldr.append(colmodeljson);
		jqbldr.append(",");
		
		jqbldr.append("\"gridData\" :");
		jqbldr.append(griddatajson);
		
		jqbldr.append("}");

		jqgridresp = jqbldr.toString();
		return jqgridresp;
	}

	/*
	 * Create String of JSON of column names for jqGrid colNames
	 */
	@SuppressWarnings("rawtypes")
	public static String avroSRColNamestoJQcolNames(Class avroClass) {
		String colnamejson=null;

		Schema schema = ReflectData.get().getSchema(avroClass);
		List<Schema.Field> schemaFields = schema.getFields();

		List<String> schemaFieldNames = new ArrayList<String>();
		for (Schema.Field thisField : schemaFields) {
			schemaFieldNames.add(thisField.name());
		}
				
		ObjectMapper mapper = new ObjectMapper();
		try {
			colnamejson = mapper.writeValueAsString(schemaFieldNames);
		} catch (JsonProcessingException jpe) {
			LOGGER.warn("Unable to create JSON colNames from schemaFieldNames. Returning emtpy JSON array. Exception: ",jpe);
			colnamejson = "[]";
		}

		return colnamejson;
	}

	/*
	 * Create String of JSON of column names and some parameters for jqGrid colModel
	 */
	@SuppressWarnings("rawtypes")
	public static String avroSRColNamestoJQcolModel(Class avroClass) {
		String colmodeljson=null;

		Schema schema = ReflectData.get().getSchema(avroClass);
		List<Schema.Field> schemaFields = schema.getFields();

		List<Map<String,Object>> schemaColModel = new ArrayList<Map<String,Object>>();

		
		//List<String> schemaFieldNames = new ArrayList<String>();
		for (Schema.Field thisField : schemaFields) {
			//schemaFieldNames.add(thisField.name());
			
			Map<String,Object> params = new HashMap<String,Object>();
			
			params.put("name", thisField.name());
			params.put("sortable", true);

			schemaColModel.add(params);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			colmodeljson = mapper.writeValueAsString(schemaColModel);
		} catch (JsonProcessingException jpe) {
			LOGGER.warn("Unable to create JSON colModel from schemaColModel. Returning emtpy JSON array. Exception: ",jpe);
			colmodeljson = "[]";
		}
	
		return colmodeljson;
	}
}
