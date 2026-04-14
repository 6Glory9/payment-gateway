package org.example.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DigitalEnvelope implements Serializable {


    private String algorithmType;

    private String algorithmKey;

    private String base64;

}
