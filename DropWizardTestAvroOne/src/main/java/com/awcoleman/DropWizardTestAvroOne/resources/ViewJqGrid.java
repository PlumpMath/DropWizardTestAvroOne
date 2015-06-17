package com.awcoleman.DropWizardTestAvroOne.resources;

import io.dropwizard.auth.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.awcoleman.DropWizardTestAvroOne.views.AvroRecordsForJqGridView;

@Produces(MediaType.TEXT_HTML)
@Path("/viewjqgrid")
public class ViewJqGrid {

	@GET
	@Path("/{datasetid}")
	public AvroRecordsForJqGridView showContact(@PathParam("datasetid") String datasetid, @Auth Boolean isAuthenticated) {
	      return new AvroRecordsForJqGridView(datasetid);
	}
}