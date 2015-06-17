package com.awcoleman.DropWizardTestAvroOne;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class DWTestAvroOneConfiguration extends Configuration {
	
	@JsonProperty
	private String message;

	@JsonProperty
	private String avrotablename;
	
	@JsonProperty
	private String avrokeycol;
	
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();

	public String getMessage() {
		return message;
	}

	public String getAvrotablename() {
		return avrotablename;
	}

	public String getAvrokeycol() {
		return avrokeycol;
	}

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

}
