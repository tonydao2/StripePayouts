package com.example.stripepayouts.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WebHookDTO
{
    @JsonProperty("webhook")
    public WebHookMetaData webhook;
    @JsonProperty("data")
    public List<OrderlineDTO> data;
}
