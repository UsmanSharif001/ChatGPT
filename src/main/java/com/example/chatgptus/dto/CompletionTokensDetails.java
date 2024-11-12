
package com.example.chatgptus.dto;

import java.util.LinkedHashMap;
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
    "reasoning_tokens",
    "audio_tokens",
    "accepted_prediction_tokens",
    "rejected_prediction_tokens"
})
@Generated("jsonschema2pojo")
public class CompletionTokensDetails {

    @JsonProperty("reasoning_tokens")
    private Integer reasoningTokens;
    @JsonProperty("audio_tokens")
    private Integer audioTokens;
    @JsonProperty("accepted_prediction_tokens")
    private Integer acceptedPredictionTokens;
    @JsonProperty("rejected_prediction_tokens")
    private Integer rejectedPredictionTokens;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("reasoning_tokens")
    public Integer getReasoningTokens() {
        return reasoningTokens;
    }

    @JsonProperty("reasoning_tokens")
    public void setReasoningTokens(Integer reasoningTokens) {
        this.reasoningTokens = reasoningTokens;
    }

    @JsonProperty("audio_tokens")
    public Integer getAudioTokens() {
        return audioTokens;
    }

    @JsonProperty("audio_tokens")
    public void setAudioTokens(Integer audioTokens) {
        this.audioTokens = audioTokens;
    }

    @JsonProperty("accepted_prediction_tokens")
    public Integer getAcceptedPredictionTokens() {
        return acceptedPredictionTokens;
    }

    @JsonProperty("accepted_prediction_tokens")
    public void setAcceptedPredictionTokens(Integer acceptedPredictionTokens) {
        this.acceptedPredictionTokens = acceptedPredictionTokens;
    }

    @JsonProperty("rejected_prediction_tokens")
    public Integer getRejectedPredictionTokens() {
        return rejectedPredictionTokens;
    }

    @JsonProperty("rejected_prediction_tokens")
    public void setRejectedPredictionTokens(Integer rejectedPredictionTokens) {
        this.rejectedPredictionTokens = rejectedPredictionTokens;
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
