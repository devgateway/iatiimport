package org.devgateway.importtool.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.devgateway.importtool.dao.FileRepository;
import org.devgateway.importtool.dao.ProjectRepository;
import org.devgateway.importtool.model.File;
import org.devgateway.importtool.model.ImportSummary;
import org.devgateway.importtool.model.Project;
import org.devgateway.importtool.security.ImportSessionToken;
import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.processor.helper.IDocumentMapper;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@org.springframework.stereotype.Service
public class ImportService {
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private ProjectRepository projectRepository;

	public ImportSummary getSummary(IDocumentMapper documentMapper, ImportSessionToken importSessionToken, ISourceProcessor processor){
		ImportSummary importSummmary = new ImportSummary();
		importSummmary.setProjectCount(documentMapper.getDocumentMappings().size());
		importSummmary.setFieldMappingCount(documentMapper.getFieldMappingObject().size());

		if (fileRepository != null || importSessionToken != null) {
			importSummmary.setFileCount(fileRepository.countBySessionId(importSessionToken.getImportTokenSessionId()));
		}

		if (processor != null) {
			List<Field> fields = processor.getFilterFields();
			importSummmary.setFilterCount(fields.stream().filter(f -> f.getFilters().size() > 0).count());
		}

		// value mapping count
		int mappedValuesCount = 0;
		for (FieldValueMapping mapping : documentMapper.getValueMappingObject()) {
			for (Map.Entry<Integer, Integer> entry : mapping.getValueIndexMapping().entrySet()) {
				if (entry.getValue() != null) {
					++mappedValuesCount;
				}

			}
		}
		importSummmary.setValueMappingCount(mappedValuesCount);
		return importSummmary;
	}

	public File uploadFile(MultipartFile file, ISourceProcessor srcProcessor, ImportSessionToken authToken) throws IOException{
		InputStream is = new ByteArrayInputStream(file.getBytes());
		srcProcessor.setInput(is);
		File uploadedFile = new File();
		uploadedFile.setData(file.getBytes());
		uploadedFile.setCreatedDate(new Date());
		uploadedFile.setFileName(file.getOriginalFilename());
		uploadedFile.setAuthor(authToken.getAuthenticationToken());
		uploadedFile.setSessionId(authToken.getImportTokenSessionId());
		fileRepository.save(uploadedFile);
		return uploadedFile;
	}

	public void insertLog(ActionResult result, Long id) {
		Project project = new Project();
		File file = fileRepository.findById(id);
		project.setFile(file);
		project.setTitle(result.getMessage());
		project.setNotes(result.getOperation());
		project.setStatus(result.getStatus());
		projectRepository.save(project);
	}
	
	public void deleteImport(Long id){
		projectRepository.deleteByFileId(id);
		fileRepository.delete(id);
	}
	
}
