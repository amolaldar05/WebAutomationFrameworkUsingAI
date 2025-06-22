package org.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonUtils {
    private String filePath;
    private JsonNode rootNode;

    public JsonUtils(String filePath) throws IOException {
        this.filePath = filePath;
        ObjectMapper mapper = new ObjectMapper();
        rootNode = mapper.readTree(new File(filePath));
    }

    public JsonNode getRootNode() {
        return rootNode;
    }

    public String getValue(String... path) {
        JsonNode node = rootNode;
        for (String key : path) {
            if (node == null) return null;
            node = node.get(key);
        }
        return node != null ? node.asText() : null;
    }

    public List<String> getArrayValues(String... path) {
        List<String> values = new ArrayList<>();
        JsonNode node = rootNode;
        for (String key : path) {
            if (node == null) return values;
            node = node.get(key);
        }
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                values.add(item.asText());
            }
        }
        return values;
    }

    public void close() {
        // No resources to close for Jackson
    }
}

