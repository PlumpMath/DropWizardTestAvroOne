package com.awcoleman.DropWizardTestAvroOne.views;

import io.dropwizard.views.View;

public class AvroRecordsForJqGridView extends View {
	private final String datasetID;

	public AvroRecordsForJqGridView(String datasetID) {
		super("/views/index.mustache");
		this.datasetID = datasetID;
	}

	public String getDatasetID() {
		return datasetID;
	}
}