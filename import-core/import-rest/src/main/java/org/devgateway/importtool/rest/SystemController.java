package org.devgateway.importtool.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.devgateway.importtool.model.SystemInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/system")
class SystemController {
    @RequestMapping(method = GET, value = "/{status}")
    ResponseEntity<SystemInfo> getStatus() {    	
    	return new ResponseEntity<>(new SystemInfo(), HttpStatus.OK);
    }

}
