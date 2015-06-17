package com.awcoleman.DropWizardTestAvroOne;

import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.awcoleman.DropWizardTestAvroOne.resources.AvroRecordsForJqGridResource;
import com.awcoleman.DropWizardTestAvroOne.resources.AvroRecordsResource;
import com.awcoleman.DropWizardTestAvroOne.resources.ViewJqGrid;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

/**
 * Main Entry to DropWizard
 *
 * This is an example DropWizard application to fetch data from an Avro-backed Hive table.
 * The HTTP auth username and password are set in DWTestAvroOneAuthenticator as test/test
 * The Hive database name, table name, and key column name are in config.yaml
 * The Avro schema is in src/main/avro
 * To change to a different schema, change:
 *    com.awcoleman.DropWizardTestAvroOne.dao.mappers.AvroRecordMapper
 *    		BeanMapper<TestRecOne>
 *    		super(TestRecOne.class)
 *    com.awcoleman.DropWizardTestAvroOne.resources.AvroRecordsResource
 *    		avroSRClass = com.awcoleman.examples.DropWizardTestAvroOne.avro.TestRecOne.class
 *	  com.awcoleman.DropWizardTestAvroOne.resources.AvroRecordsForJqGridResource
 *			avroSRClass = com.awcoleman.examples.DropWizardTestAvroOne.avro.TestRecOne.class
 *
 *
 * In Dev env in Eclipse, run as
 *   cd ~/git/DropWizardTestAvroOne/DropWizardTestAvroOne/
 *   java -jar ./target/DropWizardTestAvroOne-1.0-SNAPSHOT.jar server config.yaml
 * In Prod, run as
 *   java -jar DropWizardTestAvroOne-1.0-SNAPSHOT.jar server config.yaml
 * 
 * After start, to get html page: http://localhost:8080/api/viewjqgrid/AAA2
 *   to get base Avro: curl -u test:test http://localhost:8080/api/avrorecords/AAA1 | python -m json.tool
 *   to get Avro data wrapped for jqGrid: curl -u test:test http://localhost:8080/api/avrorecforjqgrid/AAA3 | python -m json.tool
 * 
 *
 */
public class App extends Application<DWTestAvroOneConfiguration> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);


	@Override
	public void initialize(Bootstrap<DWTestAvroOneConfiguration> dwtaoConfigurationBootstrap) {

		dwtaoConfigurationBootstrap.addBundle(new AssetsBundle("/assets", "/assets", "", "basicBundle"));
		dwtaoConfigurationBootstrap.addBundle(new ViewBundle());
	}

	@Override
	public void run(DWTestAvroOneConfiguration c, Environment e)throws Exception {
		LOGGER.info("DropWizardTestAvroOne run. Initial message: "+c.getMessage());

		// Create a DBI factory and build a JDBI instance
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(e, c.getDataSourceFactory(), "maindb");


		// Register the authenticator with the environment
		e.jersey().register(AuthFactory.binder(new BasicAuthFactory<Boolean>(new DWTestAvroOneAuthenticator(),
				"DropWizardTestAvroOne Auth Realm",Boolean.class)));


		// Add the resources to the environment
		e.jersey().register(new AvroRecordsResource(jdbi,c ));
		e.jersey().register(new AvroRecordsForJqGridResource(jdbi, c));

		e.jersey().register(new ViewJqGrid());

	}

	public static void main( String[] args ) throws Exception {
		new App().run(args);
	}
}
