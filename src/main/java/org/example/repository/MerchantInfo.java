package org.example.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class MerchantInfo implements Serializable {
    private String merchantNo;
}
