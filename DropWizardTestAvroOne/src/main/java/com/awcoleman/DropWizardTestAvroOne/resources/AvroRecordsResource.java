package com.awcoleman.DropWizardTestAvroOne.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.dropwizard.auth.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.avro.specific.SpecificRecordBase;
import org.skife.jdbi.v2.DBI;

import com.awcoleman.DropWizardTestAvroOne.DWTestAvroOneConfiguration;
import com.awcoleman.DropWizardTestAvroOne.dao.AvroRecordDAO;

/*
 * Return JSON data for Set of Avro SpecificRecord(s)
 * 
 */
@Path("/avrorecords")
@Produces(MediaType.APPLICATION_JSON)
public class AvroRecordsResource {
		
	private final AvroRecordDAO avrorecDao;

	DWTestAvroOneConfiguration c;
	
	public AvroRecordsResource(DBI jdbi, DWTestAvroOneConfiguration ic) {
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

			retResp = Response.ok(avrorecswrapper).build();
		} else {
			//if no records in set, create empty set so JSON output is similar to JSON with records (Could just change this to send "[]").
			HashSet<ArrayList<String>> emptywrapper = new HashSet<ArrayList<String>>();
			ArrayList<String> emptyInner = new ArrayList<String>();
			emptywrapper.add(emptyInner);
			retResp = Response.ok(emptywrapper).build();
		}
		return retResp;
	}
}
