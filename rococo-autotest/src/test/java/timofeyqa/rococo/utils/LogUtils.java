package timofeyqa.rococo.utils;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility to mask long parameters in logged requests/responses.
 *
 * <p>Implements {@link TemplateMethodModelEx} so it can be directly
 * instantiated and used inside FreeMarker templates.</p>
 */
public class LogUtils implements TemplateMethodModelEx {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_LENGTH = 2010;
    private static final Set<String> SENSITIVE_KEYS = Set.of("content", "avatar", "photo");

    /**
     * Masks sensitive long parameters in the provided string.
     *
     * @param body original body
     * @return body with long parameters replaced with &lt;long_param&gt;
     */
    public String maskLongParams(String body) {
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

    private boolean maskNode(JsonNode node) {
        boolean modified = false;
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                if (SENSITIVE_KEYS.contains(key) && value.isTextual() && value.asText().length() > MAX_LENGTH) {
                    obj.put(key, "<long_param>");
                    modified = true;
                } else if (maskNode(value)) {
                    modified = true;
                }
            }
        } else if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                if (maskNode(arr.get(i))) {
                    modified = true;
                }
            }
        }
        return modified;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments == null || arguments.isEmpty()) {
            return "";
        }
        Object arg = arguments.get(0);
        String body = arg == null ? null : arg.toString();
        return maskLongParams(body);
    }
}
