package com.greenario;

import com.greenario.cropmanagement.ProductionCell;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchProductionCellHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(FetchProductionCellHandler.class);

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Get the production cell ID from the work item parameters
        String productionCellId = (String) workItem.getParameter("productionCellId");

        if (productionCellId == null || productionCellId.isEmpty()) {
            logger.error("Production cell ID is missing or empty.");
            manager.completeWorkItem(workItem.getId(), new HashMap<>()); // Return an empty result if ID is invalid
            return;
        }

        // Call the external service to fetch the production cell details
        ProductionCell productionCell = fetchProductionCellFromService(productionCellId);

        if (productionCell == null) {
            logger.error("Failed to fetch production cell with ID: " + productionCellId);
        }

        // Create the result map
        Map<String, Object> results = new HashMap<>();
        results.put("productionCell", productionCell);

        // Complete the work item
        manager.completeWorkItem(workItem.getId(), results);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Handle work item abortion
        logger.info("Work item with ID " + workItem.getId() + " has been aborted.");
    }

    private ProductionCell fetchProductionCellFromService(String productionCellId) {
        // Replace with your actual API URL
        String url = "http://localhost:9090/greenario/api/v1/productioncell/" + productionCellId;

        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            // Log the response for debugging
            logger.debug("Response from API: " + jsonResponse);

            // Convert JSON response to ProductionCell object
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse, ProductionCell.class);

        } catch (Exception e) {
            logger.error("Error fetching production cell from service", e);
            return null;
        }
    }
}

