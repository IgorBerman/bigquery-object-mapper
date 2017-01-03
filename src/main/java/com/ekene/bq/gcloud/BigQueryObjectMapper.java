package com.ekene.bq.gcloud;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * The BigQueryObjectMapper helps to map a POJO to a BQ Row and generate a BQ schema based on the POJO using reflection
 */

public class BigQueryObjectMapper {

    private static final Logger LOG = LoggerFactory.getLogger(BigQueryObjectMapper.class);

    /**
     * Generates a BigQuery Table row based on the supplied Object
     *
     * @param obj Object to be mapped
     * @return A TableRow
     */
    public static TableRow generateTableRow(Object obj) throws Exception {

        TableRow row = new TableRow();

        for (Field field : getFields(obj.getClass())) {

            try {

                Object value = getFieldValue(field, obj);

                if (value != null)
                    if (IRepeatedRecord.class.isAssignableFrom(value.getClass())) {

                        List<TableRow> repeatedRecords = new ArrayList<>();

                        for (Object o : ((IRepeatedRecord) value).getRecords()) {
                            repeatedRecords.add(generateTableRow(o));
                        }

                        row.set(field.getName(), repeatedRecords);

                    } else if (IRepeatedValues.class.isAssignableFrom(value.getClass())) {

                        List<Object> repeatedValues = new ArrayList<>();

                        for (Object o : ((IRepeatedValues) value).getValues()) {
                            repeatedValues.add(o);
                        }

                        row.set(field.getName(), repeatedValues);

                    } else if (IRecord.class.isAssignableFrom(value.getClass())) {

                        row.set(field.getName(), generateTableRow(value));

                    } else if (Date.class.isAssignableFrom(value.getClass()))
                        row.set(field.getName(), ((Date) value).toInstant().toString());
                    else if (String.class.isAssignableFrom(value.getClass())) {
                        if (value.toString().isEmpty())
                            row.set(field.getName(), null);
                        else
                            row.set(field.getName(), value);
                    } else
                        row.set(field.getName(), value);
                else
                    row.set(field.getName(), null);

            } catch (Exception e) {
                LOG.error("Error in BigQueryObjectMapper:" + e.getMessage());
                throw e;
            }


        }

        return row;
    }

    /**
     * Generates a BigQuery Schema as a JSON String based on the supplied Class
     *
     * @param c Class generate a schema for
     * @return JSONArray as String
     */
    public static String generateJsonSchema(Class<?> c) {
        Gson g = new Gson();

        return g.toJson(getBQColumns(getBQFieldList(c)).toArray());
    }

    private static ArrayList<BigQueryColumn> getBQColumns(List<TableFieldSchema> fields) {

        ArrayList<BigQueryColumn> columns = new ArrayList<>();

        for (TableFieldSchema f : fields) {

            BigQueryColumn bqc = new BigQueryColumn();

            if (f.getType().toUpperCase().equals("RECORD")) {
                bqc.setName(f.getName());
                bqc.setDataType(f.getType());
                bqc.setMode(f.getMode());
                bqc.setFields(getBQColumns(f.getFields()).toArray(new BigQueryColumn[]{}));
            } else {
                bqc.setName(f.getName());
                bqc.setDataType(f.getType());
                bqc.setMode(f.getMode());
            }
            columns.add(bqc);
        }
        return columns;
    }

    /**
     * Generates a BigQuery TableSchema based on the supplied Class
     *
     * @param c Class generate a schema for
     * @return A TableSchema
     */
    public static TableSchema generateTableSchema(Class<?> c) {

        return new TableSchema().setFields(getBQFieldList(c));

    }

    private static List<TableFieldSchema> getBQFieldList(Class<?> c) {

        ArrayList<TableFieldSchema> schema = new ArrayList<>();

        for (Field f : getFields(c)) {

            if (IRepeatedRecord.class.isAssignableFrom(f.getType())) {

                Class<?> recordType = (Class<?>) ((ParameterizedType) f.getType().getGenericInterfaces()[0]).getActualTypeArguments()[0];

                schema.add(new TableFieldSchema()
                        .setName(f.getName())
                        .setType("RECORD")
                        .setMode("REPEATED")
                        .setFields(getBQFieldList(recordType)));

            } else if (IRecord.class.isAssignableFrom(f.getType())) {

                schema.add(new TableFieldSchema()
                        .setName(f.getName())
                        .setType("RECORD")
                        .setMode("NULLABLE")
                        .setFields(getBQFieldList(f.getType())));

            } else {

                schema.add(new TableFieldSchema()
                        .setName(f.getName())
                        .setType(getBigQueryDataType(f.getType()))
                        .setMode(IRepeatedValues.class.isAssignableFrom(f.getType()) ? "REPEATED" : "NULLABLE"));

            }
        }

        return schema;
    }

    /**
     * Determine's a best BigQuery data type for for Java types
     * This handle just primitive types, complex types default to string
     *
     * @param c Object class type
     * @return BigQuery DataType Name as string
     */
    private static String getBigQueryDataType(Class<?> c) {

        String BigQueryDataType;


        if (IRepeatedValues.class.isAssignableFrom(c)) {

            Class<?> valueType = (Class<?>) ((ParameterizedType) c.getGenericInterfaces()[0]).getActualTypeArguments()[0];

            BigQueryDataType = getBigQueryDataType(valueType);

        } else {

            if (int.class.isAssignableFrom(c) || short.class.isAssignableFrom(c) || long.class.isAssignableFrom(c)
                    || Integer.class.isAssignableFrom(c) || Short.class.isAssignableFrom(c) || Long.class.isAssignableFrom(c))
                BigQueryDataType = "INTEGER";
            else if (double.class.isAssignableFrom(c) || Double.class.isAssignableFrom(c))
                BigQueryDataType = "FLOAT";
            else if (Date.class.isAssignableFrom(c))
                BigQueryDataType = "TIMESTAMP";
            else if (boolean.class.isAssignableFrom(c) || Boolean.class.isAssignableFrom(c))
                BigQueryDataType = "BOOLEAN";
            else
                BigQueryDataType = "STRING";
        }
        return BigQueryDataType;
    }

    /**
     * Get a field's value
     *
     * @param field The field
     * @param obj   An instance of the object
     * @return Field's value
     */
    private static Object getFieldValue(Field field, Object obj) throws Exception {

        try {

            // Attempt to get the field value assuming the field is public
            return field.get(obj);

        } catch (IllegalAccessException ia) {
            // if the field is a private, an IllegalAccessException is thrown
            // try to access it via its getter method
            return runGetter(field, obj);
        }
    }

    /**
     * Gets a list of all fields of a class including inherited fields
     *
     * @param type Class to get its fields
     * @return A List of Field objects
     */
    private static List<Field> getFields(Class<?> type) {

        ArrayList<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {

            Field[] f = c.getDeclaredFields();
            ArrayUtils.reverse(f);

            for (Field v : f) {

                if (fields.stream().filter(x -> x.getName().equals(v.getName()))
                        .findAny()
                        .orElse(null) == null)
                    fields.add(v);
            }
        }

        Collections.reverse(fields);
        return fields;
    }

    /**
     * Runs the getter method of a Field
     *
     * @param field The field object
     * @param o     The Object instance
     * @return Field value
     */
    private static Object runGetter(Field field, Object o) throws Exception {
        for (Method method : getMethods(o.getClass())) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3))) {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                    return method.invoke(o);
                }
            }
        }
        return null;
    }

    /**
     * Gets all getter Methods of a Class
     *
     * @param type Class to get its getter methods
     * @return A stack of Method objects
     */
    private static List<Method> getMethods(Class<?> type) {

        List<Method> result = new ArrayList<>();

        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            Method[] methods = c.getDeclaredMethods();

            result.addAll(Arrays.asList(methods));
        }

        return result;
    }

    /**
     * POJO for BigQuery Columns
     */
    private static class BigQueryColumn {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("mode")
        @Expose
        private String mode = "nullable";
        @SerializedName("fields")
        @Expose
        private BigQueryColumn[] fields;

        public BigQueryColumn() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getDataType() {
            return type;
        }

        public void setDataType(String type) {
            this.type = type;
        }

        public BigQueryColumn[] getFields() {
            return fields;
        }

        public void setFields(BigQueryColumn[] fields) {
            this.fields = fields;
        }

    }

}

