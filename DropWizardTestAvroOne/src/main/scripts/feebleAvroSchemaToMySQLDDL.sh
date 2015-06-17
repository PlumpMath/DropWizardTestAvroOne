#!/bin/bash

# This is a very hackish script that tries to convert an Avro schema to a mySQL DDL
#   (and with small modifications works fine for PostgreSQL and MS SQL Server)
# Sometimes works well, and sometimes fails miserably...
#
# My long-term plan is to look at Hibernate's DDL creation methods and see if I can get
#   an Avro schema into those methods to get all the supported DDL flavors out...
#
# Argument 1 is an Avro schema in the normal JSON format
# Argument 2 is an Avro record name (usually "name" : "XYZ" towards the top of the schema, XYZ goes here)
# Argument 3 is the filename to output the create table DDL
# Argument 4 is the tablename for the create table DDL statement
#
# Does not handle default and lots of other field modifiers. I usually just correct those by hand on the output.
#

if [ "$#" -ne 4 ]; then
    echo "Requires 4 arguments: input filename of avro schema, avro record name, output filename for the create table ddl, and table name. Exiting."
    exit
fi

inputfn=$1
recn=$2
outputfn=$3
tablen=$4

#Create tempdir for working space
workdir=$(mktemp -dt "$(basename $0).XXXXXXXXXX")

#Copy Avro schema
workfn=${workdir}/schemaddl
cp $inputfn $workfn

echo "Using working dir of $workdir"

sed -i "/\"name\"\s*:\s*\"${recn}\"/d" ${workfn}
sed -i "/\"namespace\"\s*:/d" ${workfn}
sed -i "/\"type\"\s*:\s*\"record\"/d" ${workfn}
sed -i "/\"doc\"\s*:/d" ${workfn}
sed -i "/\"fields\"\s*:/d" ${workfn}

#Add any additional user-defined meta fields here.
sed -i "/\"schemaLastUpdateDate\"\s*:/d" ${workfn}
sed -i "/\"schemaLastUpdateBy\"\s*:/d" ${workfn}
sed -i "/\"notes\"\s*:/d" ${workfn}

#Convert field types (not a complete list of avro primitive types, just the ones I normally use)
defvarchar="VARCHAR(250)"
sed -i "s/\"type\"\s*:\s*\"string\"/ ${defvarchar},/" ${workfn}
sed -i "s/\"type\"\s*:\s*\[\s*\"null\"\s*,\s*\"string\"\s*\]/ ${defvarchar},/" ${workfn}
sed -i "s/\"type\"\s*:\s*\"int\"/ int,/" ${workfn}
sed -i "s/\"type\"\s*:\s*\[\s*\"null\"\s*,\s*\"int\"\s*\]/ int,/" ${workfn}
sed -i "s/\"type\"\s*:\s*\"long\"/ bigint,/" ${workfn}
sed -i "s/\"type\"\s*:\s*\[\s*\"null\"\s*,\s*\"long\"\s*\]/ bigint,/" ${workfn}
sed -i "s/\"type\"\s*:\s*\"float\"/ float,/" ${workfn}
sed -i "s/\"type\"\s*:\s*\[\s*\"null\"\s*,\s*\"float\"\s*\]/ float,/" ${workfn}
sed -i "s/\"type\"\s*:\s*\"double\"/ double,/" ${workfn}
sed -i "s/\"type\"\s*:\s*\[\s*\"null\"\s*,\s*\"double\"\s*\]/ double,/" ${workfn}
sed -i "s/\"type\"\s*:\s*\"boolean\"/ boolean,/" ${workfn}
sed -i "s/\"type\"\s*:\s*\[\s*\"null\"\s*,\s*\"boolean\"\s*\]/ boolean,/" ${workfn}

#Clean up all the extra punctuation left over
sed -i "s/\"name\"\s*:\s*\"//" ${workfn}
sed -i 's/",//g' ${workfn}
sed -i 's/,,/,/g' ${workfn}
sed -i 's/},\s*{//g' ${workfn}
sed -i 's/{//g' ${workfn}
sed -i 's/}\s*],//g' ${workfn}
sed -i 's/}//g' ${workfn}
tr -d '\r' < ${workfn} > ${workfn}.tmp && mv ${workfn}.tmp ${workfn} #Replace crlf with lf
tr -s '\n' ' ' < ${workfn} > ${workfn}.tmp && mv ${workfn}.tmp ${workfn} #Replace newline with space
tr -s '[:space:]' < ${workfn} > ${workfn}.tmp && mv ${workfn}.tmp ${workfn}  #make multiple spaces single spaces
sed -i '$s/,\s*$/\);/g' ${workfn}
sed -i 's/,,/,/g' ${workfn}
sed -i 's/,\s*\]/)/g' ${workfn}
sed -i 's/\]/)/g' ${workfn}

#Add table name
echo "CREATE TABLE ${tablen} (" > ${workfn}.tmp

#Finish
cat ${workfn} >> ${workfn}.tmp
mv ${workfn}.tmp ${outputfn}

#Show output, may still need manual cleanup
echo "Console output of ${outputfn}. May still need manual cleanup."
echo ""
echo ""
cat ${outputfn}
echo ""
echo ""

echo "Please manually cleanup: ${workdir}."
echo "Finished successfully. See ${outputfn}"
