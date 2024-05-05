package com.example.demo.config;

import com.example.demo.openai.FunctionTool;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "todo")
@Data
public class TodoToolsConfig {
    private ToolConfig add;
    private ToolConfig delete;
    private ToolConfig list;

    public FunctionTool buildAddTool() {
        return this.buildTool(add);
    }

    public FunctionTool buildDeleteTool() {
        return this.buildTool(delete);
    }

    public FunctionTool buildListTool() {
        return this.buildTool(list);
    }

    private FunctionTool buildTool(ToolConfig toolConfig) {
        FunctionTool tool = new FunctionTool(
                new FunctionTool.Function(toolConfig.name, toolConfig.description)
        );

        toolConfig.properties.forEach(
                property ->
                        tool.getFunction().getParameters().addProperty(
                                property.name,
                                new FunctionTool.Function.Parameters.Property(
                                        "string",
                                        property.getDescription()
                                ),
                                property.isRequired()
                        )
        );

        return tool;
    }

    @Data
    public static class ToolConfig {
        @NotEmpty
        private String name;
        @NotEmpty
        private String description;
        @NotNull
        private List<Property> properties = new ArrayList<>();

        @Data
        public static class Property {
            @NotEmpty
            private String name;
            @NotEmpty
            private String description;
            private boolean required;
        }
    }
}