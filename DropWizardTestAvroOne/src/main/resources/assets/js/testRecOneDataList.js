//Define some globals
var datasetid;
var jqgdata;
var jqgColNames;
var jqgColModel;
var jqgdataloaded=false;

//Get jqGrid data
function loadJqGriddata() {
	//Get the datasetid to load from the HTML (set by mustache). Could use many other methods.
	var dsdiv = document.getElementById('datasetid');
	datasetid = dsdiv.getAttribute('data-val');
	if (!datasetid) datasetid="AAA1";  //set a base value from the example dataset if nothing is specified
	
	//Load the data for jqGrid from the server
	var jqgdataresp = $.ajax({ url: "/api/avrorecforjqgrid/"+datasetid, dataType: 'json', async: true, success: function(alljqgdata, result) {
		//console.log( DumpObjectIndented(ijqgdata, 4) );

		jqgdata = promoteAvroNullable(alljqgdata.gridData);
		jqgdataloaded = true;   //Set data loaded flag to true
		
		//jqgrid col variables from json
		jqgColNames = alljqgdata.colNames;
		jqgColModel = alljqgdata.colModel;
	} }).responseText;
}

/*
 * For Avro-Specific JSON generation (but should not effect non-avro generated json). Promote avro nullable/optional fields (null union) inner value to just the value.
 * Only tested on simple schemas. For more complicated schemas, could iterate further than one level and could specifically check for avro primitive types
 */
function promoteAvroNullable(idata) {
	for (var ii = 0; ii < idata.length; ii++) {
		var thisrow = idata[ii];
		for (var prop in thisrow) {
			if (thisrow[prop] && thisrow.hasOwnProperty(prop)) {  //thisrow.prop should cause nulls to be skipped
				//if this field is an object then it is an optional union (for our simple schemas)
				if (typeof(thisrow[prop]) === 'object') {
					//Grab first (really only property of this field
					for (var key in thisrow[prop]) {
						if (thisrow[prop].hasOwnProperty(key)) {
							break;
						}
					}
					//now key is the right property (such as int,string,etc)
					//promote inner property
					thisrow[prop] = thisrow[prop][key];
				}
			}
		}
	}
	return idata;
}

function showJQGDataList(jqgColNames,jqgColModel,jqgdata) {
	$("#testRecOneDataList").jqGrid({
		datatype: "local", 
		data: jqgdata,
		colNames: jqgColNames,
		colModel: jqgColModel,
		height: 'auto',
		sortable: true,
		width: null,
		shrinkToFit: false,
		loadonce: true,
		caption: "DatasetID "+datasetid,
		beforeRequest: function() {
			//Add any changes to the colModel here (does not error if cols do not exist)
			$(this).jqGrid('setColProp', 'fieldOne', { cellattr: function  (rowId, val, rawObject) { var tooltip="Helpful Tip"; return ' title="'+tooltip+'"'; } });
		}, //End beforeRequest
		loadComplete: function() {
			//Add anything to run after grid load
		} //End loadComplete
	});
}

//Pretty-print object
//usage: console.log( DumpObjectIndented(obj, 4) );
function DumpObjectIndented(obj, indent) {
	var result = "";
	var indi = parseInt(indent);
	if (indent == null || !((indi>=0)||(indi<=20))) indi = 4;
	var inds = "";
	for (i=0; i<indi; i++) { 
		inds = inds+" ";
	}

	for (var property in obj)
	{
		var value = obj[property];
		if (typeof value == 'string')
			value = "'" + value + "'";
		else if (typeof value == 'object')
		{
			if (value instanceof Array)
			{
				// Just let JS convert the Array to a string!
				value = "[ " + value + " ]";
			}
			else
			{
				// Recursive dump
				// (replace "  " by "\t" or something else if you prefer)
				var od = DumpObjectIndented(value, inds + "  ");
				// If you like { on the same line as the key
				//value = "{\n" + od + "\n" + indent + "}";
				// If you prefer { and } to be aligned
				value = "\n" + inds + "{\n" + od + "\n" + inds + "}";
			}
		}
		result += inds + "'" + property + "' : " + value + ",\n";
	}
	return result.replace(/,\n$/, "");
}

//Load data
loadJqGriddata();

//Wait until data is loaded before showing the jqgrid
var _flagCheckJqgd = setInterval(function() {
	if (jqgdataloaded==true) {
		clearInterval(_flagCheckJqgd);
		showJQGDataList(jqgColNames,jqgColModel,jqgdata); // the function to run once all flags are true
	}
}, 50); // interval set at X milliseconds