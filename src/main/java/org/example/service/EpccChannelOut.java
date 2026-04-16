package org.example.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
public class EpccChannelOut implements Serializable {

    @JsonIgnore
    private String direction = "11";

    @JsonIgnore
    protected String messageType;

}
