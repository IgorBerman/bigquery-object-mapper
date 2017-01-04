# bigquery-object-mapper
The BigQueryObjectMapper is a class that makes it easy for you to convert your model objects to BigQuery Row Object and
generate a BigQuery schema based on your model objects using reflection.

`Currently Supports just LegacySQL`

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
