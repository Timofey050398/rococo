package timofeyqa.rococo.utils;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Iterator;
import java.util.Map;

public final class LogUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_LENGTH = 2010;

    private LogUtils() {
    }

    public static String maskLongParams(String body) {
        if (body == null || body.isEmpty()) {
            return body;
        }
        try {
            JsonNode node = MAPPER.readTree(body);
            if (maskNode(node)) {
                return MAPPER.writeValueAsString(node);
            }
            return body;
        } catch (Exception e) {
            return body.length() > MAX_LENGTH ? "<long_param>" : body;
        }
    }

    private static boolean maskNode(JsonNode node) {
        boolean modified = false;
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode value = entry.getValue();
                if (value.isTextual() && value.asText().length() > MAX_LENGTH) {
                    obj.put(entry.getKey(), "<long_param>");
                    modified = true;
                } else if (maskNode(value)) {
                    modified = true;
                }
            }
        } else if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                JsonNode value = arr.get(i);
                if (value.isTextual() && value.asText().length() > MAX_LENGTH) {
                    arr.set(i, TextNode.valueOf("<long_param>"));
                    modified = true;
                } else if (maskNode(value)) {
                    modified = true;
                }
            }
        }
        return modified;
    }
}
