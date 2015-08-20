package org.devgateway.importtool.rest;



import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.File;
import org.devgateway.importtool.services.FileRepository;
import org.devgateway.importtool.services.Project;
import org.devgateway.importtool.services.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/importer/reports")
class ReportController {
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private ProjectRepository projectRepository;
	private Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.GET, value = "/previousimports")
	public ResponseEntity<Page<File>> listFiles(Pageable pageable, HttpServletRequest request) {				
		Page<File> list = fileRepository.findAll(pageable);		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/projects/{fileId}")
	public ResponseEntity<Page<Project>> listProjects(Pageable pageable, @PathVariable Long fileId, HttpServletRequest request) {				
		Page<Project>  list = projectRepository.findByFileId(fileId, pageable);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

}
