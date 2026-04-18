package org.example.controller;

import org.example.service.NotifyGatewayEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@Controller
public class EpccNotifyController {

    @Autowired
    private NotifyGatewayEngine notifyGatewayEngine;

    @PostMapping(
            path = "/epcc/notify",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @ResponseBody
    public String epccNotify(@RequestHeader("MsgTp") String msgTp, @RequestHeader("OriIssrId") String issuerId,
                             @RequestBody String rawXml) {
        notifyGatewayEngine.execute(issuerId, msgTp, rawXml);
        return rawXml;
    }
}
