
package com.example.chatgptus.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "object",
    "created",
    "model",
    "choices",
    "usage",
    "system_fingerprint"
})
@Generated("jsonschema2pojo")
public class ChatResponse {

    @JsonProperty("id")
    private String id;
    @JsonProperty("object")
    private String object;
    @JsonProperty("created")
    private Integer created;
    @JsonProperty("model")
    private String model;
    @JsonProperty("choices")
    private List<Choice> choices;
    @JsonProperty("usage")
    private Usage usage;
    @JsonProperty("system_fingerprint")
    private Object systemFingerprint;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("object")
    public String getObject() {
        return object;
    }

    @JsonProperty("object")
    public void setObject(String object) {
        this.object = object;
    }

    @JsonProperty("created")
    public Integer getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(Integer created) {
        this.created = created;
    }

    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    @JsonProperty("model")
    public void setModel(String model) {
        this.model = model;
    }

    @JsonProperty("choices")
    public List<Choice> getChoices() {
        return choices;
    }

    @JsonProperty("choices")
    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    @JsonProperty("usage")
    public Usage getUsage() {
        return usage;
    }

    @JsonProperty("usage")
    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    @JsonProperty("system_fingerprint")
    public Object getSystemFingerprint() {
        return systemFingerprint;
    }

    @JsonProperty("system_fingerprint")
    public void setSystemFingerprint(Object systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
