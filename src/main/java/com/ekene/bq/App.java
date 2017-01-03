package com.ekene.bq;

import com.ekene.bq.gcloud.BigQueryObjectMapper;
import com.ekene.bq.pojos.Route;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;

public class App {

    public static void main(String[] args) throws Exception {

        // Generate a BigQuery Schema as JSON
        String schemaAsJson = BigQueryObjectMapper.generateJsonSchema(Route.class);

        // Generate a BigQuery Schema as a TableSchema object
        TableSchema schemaAsTableSchema = BigQueryObjectMapper.generateTableSchema(Route.class);

        // Maps an object BigQuery TableRow Object
        Route route = new Route();
        TableRow tableRow = BigQueryObjectMapper.generateTableRow(route);

    }
}
