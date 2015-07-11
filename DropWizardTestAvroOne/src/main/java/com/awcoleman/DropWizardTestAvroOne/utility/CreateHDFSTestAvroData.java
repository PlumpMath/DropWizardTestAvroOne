package com.awcoleman.DropWizardTestAvroOne.utility;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.awcoleman.examples.DropWizardTestAvroOne.avro.TestRecOne;
import com.google.common.io.BaseEncoding;

public class CreateHDFSTestAvroData {

	public CreateHDFSTestAvroData() throws IOException {

		
	    Configuration conf = new Configuration();
	    conf.set("fs.defaultFS", "hdfs://localhost");
	    conf.setInt("dfs.replication", 1);
	    FileSystem hdfs = FileSystem.get(conf);
		
	    
		ArrayList<TestRecOne> recList = new ArrayList<TestRecOne>();

		TestRecOne thisRec = new TestRecOne().newBuilder()
				.setDatasetID("AAA1")
				.setRecordID(1)
				.setFieldOne(true)
				.setFieldTwo(true)
				.setFieldThree(78)
				.setFieldFour(78L)
				.setFieldFive(78L)
				.setFieldSix(78.78f)
				.setFieldSeven(78.78f)
				.setFieldEight(78.78)
				.setFieldNine(78.78)
				.setFieldTen( "BEEBEE" )
				.setFieldEleven( "BEEBEE" )
				.setFieldTwelve("stringabc")
				.setFieldThirteen("stringabc")
				.build();
		recList.add(thisRec);

		thisRec = new TestRecOne().newBuilder()
				.setDatasetID("AAA2")
				.setRecordID(2)
				.setFieldOne(false)
				.setFieldTwo(null)
				.setFieldThree(15)
				.setFieldFour(15)
				.setFieldFive(null)
				.setFieldSix(15.15f)
				.setFieldSeven(null)
				.setFieldEight(15.15)
				.setFieldNine(null)
				.setFieldTen( "BEEBEE" )
				.setFieldEleven( null )
				.setFieldTwelve("stringabc")
				.setFieldThirteen(null)
				.build();
		recList.add(thisRec);

		thisRec = new TestRecOne().newBuilder()
				.setDatasetID("AAA2")
				.setRecordID(4)
				.setFieldOne(false)
				.setFieldTwo(null)
				.setFieldThree(16)
				.setFieldFour(16)
				.setFieldFive(null)
				.setFieldSix(16.16f)
				.setFieldSeven(null)
				.setFieldEight(16.16)
				.setFieldNine(null)
				.setFieldTen( "BEEBEE" )
				.setFieldEleven( null )
				.setFieldTwelve("stringabc")
				.setFieldThirteen(null)
				.build();
		recList.add(thisRec);
		
		thisRec = new TestRecOne().newBuilder()
				.setDatasetID("AAA2")
				.setRecordID(5)
				.setFieldOne(false)
				.setFieldTwo(null)
				.setFieldThree(17)
				.setFieldFour(17)
				.setFieldFive(null)
				.setFieldSix(17.17f)
				.setFieldSeven(null)
				.setFieldEight(17.17)
				.setFieldNine(null)
				.setFieldTen( "BEEBEE" )
				.setFieldEleven( null )
				.setFieldTwelve("stringabc")
				.setFieldThirteen(null)
				.build();
		recList.add(thisRec);
		
		thisRec = new TestRecOne().newBuilder()
				.setDatasetID("AAA2")
				.setRecordID(6)
				.setFieldOne(false)
				.setFieldTwo(null)
				.setFieldThree(18)
				.setFieldFour(18)
				.setFieldFive(null)
				.setFieldSix(18.18f)
				.setFieldSeven(null)
				.setFieldEight(18.18)
				.setFieldNine(null)
				.setFieldTen( "BEEBEE" )
				.setFieldEleven( null )
				.setFieldTwelve("stringabc")
				.setFieldThirteen(null)
				.build();
		recList.add(thisRec);
		
		thisRec = new TestRecOne().newBuilder()
				.setDatasetID("AAA3")
				.setRecordID(3)
				.setFieldOne(true)
				.setFieldTwo(false)
				.setFieldThree(34)
				.setFieldFour(34L)
				.setFieldFive(34L)
				.setFieldSix(34.34f)
				.setFieldSeven(34.34f)
				.setFieldEight(34.34)
				.setFieldNine(34.34)
				.setFieldTen( "BEEBEE" )
				.setFieldEleven( "BEEBEE" )
				.setFieldTwelve("stringabc")
				.setFieldThirteen("stringabc")
				.build();
		recList.add(thisRec);

		thisRec = new TestRecOne().newBuilder()
				.setDatasetID("AAA3")
				.setRecordID(7)
				.setFieldOne(true)
				.setFieldTwo(false)
				.setFieldThree(35)
				.setFieldFour(35L)
				.setFieldFive(35L)
				.setFieldSix(35.35f)
				.setFieldSeven(35.35f)
				.setFieldEight(35.35)
				.setFieldNine(35.35)
				.setFieldTen( "BEEBEE" )
				.setFieldEleven( "BEEBEE" )
				.setFieldTwelve("stringabc")
				.setFieldThirteen("stringabc")
				.build();
		recList.add(thisRec);

		
		DatumWriter<TestRecOne> spfw = new SpecificDatumWriter<TestRecOne>(TestRecOne.class);

		DataFileWriter<TestRecOne> dfwo = new DataFileWriter<TestRecOne>(spfw);
	    Path filePath = new Path("/tmp/DropWizardTestAvroOne/TestRecOne.avro");
	    OutputStream outstr = hdfs.create(filePath);
	    DataFileWriter<TestRecOne> dfw = dfwo.create(thisRec.getSchema(), outstr);
	    for (TestRecOne loopRec : recList) {
	    	dfw.append(loopRec);
		}
	    dfw.close();
	    outstr.close();
	    dfwo.close();
	}

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unused")
		CreateHDFSTestAvroData mainobj = new CreateHDFSTestAvroData();
	}

}
