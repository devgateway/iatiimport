package org.devgateway.importtool.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Optional;

import org.devgateway.importtool.services.SystemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/system")
class SystemController {
    @RequestMapping(method = GET, value = "/{status}")
    ResponseEntity<SystemInfo> getStatus() {    	
    	return new ResponseEntity<>(new SystemInfo(), HttpStatus.OK);
    }

}
