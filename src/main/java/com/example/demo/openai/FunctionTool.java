package com.example.demo.openai;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FunctionTool {
    String type = "function";
    @NonNull
    Function function;

    @Data
    public static class Function {
        @NonNull
        private final String name;
        @NonNull
        private final String description;

        Parameters parameters = new Parameters();

        @Data
        public static class Parameters {
            String type = "object";
            Map<String, Property> properties = new HashMap<>();
            List<String> required = new ArrayList<>();

            public void addProperty(String name, Property property, boolean required) {
                this.properties.put(name, property);
                if (required) {
                    this.required.add(name);
                }
            }

            @Data
            public static class Property {
                @NonNull
                String type;
                @NonNull
                String description;
            }
        }
    }
}
