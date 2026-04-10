package org.example.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
public class EpccChannelOut implements Serializable {

    private String direction = "11";

    private String messageType = "messageType";

}
