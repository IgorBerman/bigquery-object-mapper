package com.ekene.bq;

import com.ekene.bq.pojos.NestedPojoRecord;
import com.ekene.bq.pojos.TestPojo;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.ekene.bq.pojos.NestedPojo;
import com.ekene.bq.gcloud.BigQueryObjectMapper;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tests of BigQueryObjectMapper
 */
@RunWith(JUnit4.class)
public class BigQueryObjectMapperTest extends TestCase {

    private String testBigQuerySchema = "[{\"mode\":\"NULLABLE\",\"name\":\"stringValue\",\"type\":\"STRING\"},{\"mode\":\"NULLABLE\",\"name\":\"dateValue\",\"type\":\"TIMESTAMP\"},{\"mode\":\"NULLABLE\",\"name\":\"intValue\",\"type\":\"INTEGER\"},{\"mode\":\"NULLABLE\",\"name\":\"shortValue\",\"type\":\"INTEGER\"},{\"mode\":\"NULLABLE\",\"name\":\"longValue\",\"type\":\"INTEGER\"},{\"mode\":\"NULLABLE\",\"name\":\"doubleValue\",\"type\":\"FLOAT\"},{\"mode\":\"NULLABLE\",\"name\":\"booleanValue\",\"type\":\"BOOLEAN\"},{\"mode\":\"REPEATED\",\"name\":\"nestedPojo\",\"type\":\"RECORD\",\"fields\":[{\"mode\":\"NULLABLE\",\"name\":\"stringValue\",\"type\":\"STRING\"},{\"mode\":\"NULLABLE\",\"name\":\"intValue\",\"type\":\"INTEGER\"},{\"mode\":\"NULLABLE\",\"name\":\"shortValue\",\"type\":\"INTEGER\"}]}]";

    /**
     * @return the suite of tests being tested
     */
    public static junit.framework.Test suite() {
        return new TestSuite(BigQueryObjectMapperTest.class);
    }

    /**
     * Tests the the generation of BigQuery JSON schema string
     */
    @Test
    public void testGenerateJsonSchema() throws Exception {

        JSONArray expectedResult = new JSONArray(testBigQuerySchema);
        JSONArray actualResult = new JSONArray(BigQueryObjectMapper.generateJsonSchema(TestPojo.class));
        JSONAssert.assertEquals(expectedResult, actualResult, true);
    }

    /**
     * Tests the the generation of BigQuery TableSchema object
     */
    @Test
    public void testGenerateTableSchema() throws Exception {

        TableSchema expectedResult;

        JSONArray jsonArray = new JSONArray(testBigQuerySchema);
        List<TableFieldSchema> schema = getFieldList(jsonArray);

        expectedResult = new TableSchema().setFields(schema);

        TableSchema actualResult = BigQueryObjectMapper.generateTableSchema(TestPojo.class);

        Assert.assertEquals(expectedResult, actualResult);
    }

    private List<TableFieldSchema> getFieldList(JSONArray jsonArray) {

        ArrayList<TableFieldSchema> schema = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject obj = jsonArray.getJSONObject(i);

            if (obj.getString("type").toUpperCase().equals("RECORD")) {

                schema.add(new TableFieldSchema()
                        .setName(obj.getString("name"))
                        .setType(obj.getString("type"))
                        .setMode(obj.getString("mode"))
                        .setFields(getFieldList(obj.getJSONArray("fields"))));

            } else {
                schema.add(new TableFieldSchema()
                        .setName(obj.getString("name"))
                        .setType(obj.getString("type"))
                        .setMode(obj.getString("mode")));
            }
        }

        return schema;
    }

    /**
     * Tests the the generation of BigQuery TableRow object
     */
    @Test
    public void testGenerateTableRow() throws Exception {

        TableRow expectedResult = new TableRow();
        TableRow nestedExpectedResult1 = new TableRow();
        TableRow nestedExpectedResult2 = new TableRow();

        TestPojo testPojo = new TestPojo();
        NestedPojoRecord nestedPojoRecord = new NestedPojoRecord();

        NestedPojo nestedPojo1 = new NestedPojo();
        NestedPojo nestedPojo2 = new NestedPojo();

        testPojo.stringValue = "testString";
        expectedResult.set("stringValue", testPojo.stringValue);

        testPojo.dateValue = new Date();
        expectedResult.set("dateValue", testPojo.dateValue.toInstant().toString());

        testPojo.intValue = 1000000000;
        expectedResult.set("intValue", testPojo.intValue);

        testPojo.shortValue = 1000;
        expectedResult.set("shortValue", testPojo.shortValue);

        testPojo.longValue = 900000000000000L;
        expectedResult.set("longValue", testPojo.longValue);

        testPojo.doubleValue = 0.88092;
        expectedResult.set("doubleValue", testPojo.doubleValue);

        testPojo.setBooleanValue(false);
        expectedResult.set("booleanValue", testPojo.getBooleanValue());


        nestedPojo1.stringValue = "testString";
        nestedExpectedResult1.set("stringValue", nestedPojo1.stringValue);

        nestedPojo1.intValue = 1000000000;
        nestedExpectedResult1.set("intValue", nestedPojo1.intValue);

        nestedPojo1.shortValue = 1000;
        nestedExpectedResult1.set("shortValue", nestedPojo1.shortValue);

        nestedPojo2.stringValue = "testString";
        nestedExpectedResult2.set("stringValue", nestedPojo2.stringValue);

        nestedPojo2.intValue = 1000000000;
        nestedExpectedResult2.set("intValue", nestedPojo2.intValue);

        nestedPojo2.shortValue = 1000;
        nestedExpectedResult2.set("shortValue", nestedPojo2.shortValue);

        nestedPojoRecord.getRecords().add(nestedPojo1);
        nestedPojoRecord.getRecords().add(nestedPojo2);

        testPojo.nestedPojo = nestedPojoRecord;

        List<TableRow> et = new ArrayList<>();
        et.add(nestedExpectedResult1);
        et.add(nestedExpectedResult2);

        expectedResult.set("nestedPojo", et);

        TableRow actualResult = BigQueryObjectMapper.generateTableRow(testPojo);

        Assert.assertEquals(expectedResult, actualResult);
    }
}

