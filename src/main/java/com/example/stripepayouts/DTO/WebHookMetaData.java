package com.example.stripepayouts.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebHookMetaData {

    @JsonProperty("action")
    private String action;

    @JsonProperty("entity")
    private String entity;
}