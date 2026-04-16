package org.example.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@Controller
public class EpccNotifyController {

    @PostMapping(
            path = "/epcc/notify",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @ResponseBody
    public String epccNotify(@RequestHeader("MsgTp") String msgTp, @RequestHeader("OriIssrId") String issuerId,
                             @RequestBody String rawXml) {
        return rawXml;
    }
}
