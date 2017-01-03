# bigquery-object-mapper
The BigQueryObjectMapper helps to map a POJO to a BQ Row and generate a BQ schema based on the POJO using reflection

Currently Supports just LegacySQL

# Usage
```java
// Generate a BigQuery Schema as JSON
String schemaAsJson = BigQueryObjectMapper.generateJsonSchema(Route.class);

// Generate a BigQuery Schema as a TableSchema object
TableSchema schemaAsTableSchema = BigQueryObjectMapper.generateTableSchema(Route.class);

// Maps an object BigQuery TableRow Object
Route route = new Route();
TableRow tableRow = BigQueryObjectMapper.generateTableRow(route);
```
