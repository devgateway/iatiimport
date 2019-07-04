package org.devgateway.importtool.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.IDocumentMapper;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;





@org.springframework.stereotype.Service
public class DataService {
    public List<FieldValueMapping> getValueMapping(IDocumentMapper documentMapper){
                
        Map<String, Set<String>> valuesInSelectedSourceProjects = documentMapper.getValuesUsedInSelectedProjects();
        
        if (documentMapper.getFieldMappingObject() != null && documentMapper.getFieldMappingObject().size() > 0 && (documentMapper.getValueMappingObject() == null || documentMapper.getValueMappingObject().size() == 0)) {
            if (documentMapper.getValueMappingObject() == null) {
                documentMapper.setValueMappingObject(new ArrayList<FieldValueMapping>());
            }
            documentMapper.getFieldMappingObject().stream().forEach(fieldMapping -> {
                addFieldValueMapping(documentMapper, fieldMapping, valuesInSelectedSourceProjects);
            });

        } else if (documentMapper.getValueMappingObject().size() != documentMapper.getFieldMappingObject().size()) {
            documentMapper.getFieldMappingObject().stream().forEach(fieldMapping -> {
                Boolean alreadyInserted = documentMapper.getValueMappingObject().stream().anyMatch(n -> { return n.getSourceField().getUniqueFieldName().equals(fieldMapping.getSourceField().getUniqueFieldName());});
                if(!alreadyInserted) {
                    addFieldValueMapping(documentMapper, fieldMapping, valuesInSelectedSourceProjects);
                }
            });

        }
        
        return documentMapper.getValueMappingObject();
    }
    
    private void addFieldValueMapping(IDocumentMapper documentMapper, FieldMapping fieldMapping, Map<String, Set<String>> valuesInSelectedSourceProjects) {
        FieldValueMapping fvm = new FieldValueMapping();
        fvm.setSourceField(fieldMapping.getSourceField());
        fvm.setDestinationField(fieldMapping.getDestinationField());
        
        String fieldKey = "";        
        if (fieldMapping.getSourceField().getType() == FieldType.ORGANIZATION ) {
            fieldKey = fieldMapping.getSourceField().getFieldName() + "_" + fieldMapping.getSourceField().getSubType();  
        } else {
            fieldKey = fieldMapping.getSourceField().getFieldName();
        }                
                
        Set<String> valuesUsedInSelectedSourceProjects = valuesInSelectedSourceProjects.get(fieldKey);
        
        if (fieldMapping.getSourceField().getType() == FieldType.LIST 
                || fieldMapping.getSourceField().getType() == FieldType.ORGANIZATION 
                || fieldMapping.getSourceField().getType() == FieldType.LOCATION) {
            
            Field source = fieldMapping.getSourceField();
            if (source.getPossibleValues() != null) {
                source.getPossibleValues().stream().forEach(fieldValue -> {
                    
                    boolean isUsedInSelectedProjects = false;
                    if (valuesUsedInSelectedSourceProjects != null) {
                        isUsedInSelectedProjects = valuesUsedInSelectedSourceProjects.contains(fieldValue.getCode());                              
                    }                       
                    
                    if (fieldValue.isSelected() && isUsedInSelectedProjects) {                    
                        fvm.getValueIndexMapping().put(fieldValue.getIndex(), null);                    
                    }
                });
            }            
        }
        
        documentMapper.getValueMappingObject().add(fvm);
    }
    
    public List<FieldMapping> setFieldMapping(IDocumentMapper documentMapper, List<FieldMapping> fieldMapping){
        documentMapper.setFieldMappingObject(fieldMapping);     
        //update field value mappings
        List<FieldValueMapping> fieldValueMapping = new ArrayList<FieldValueMapping>();
        fieldMapping.stream().forEach(mapping -> {
            Optional<FieldValueMapping> fv = documentMapper.getValueMappingObject().stream().filter(n -> { return n.getSourceField().getUniqueFieldName().equals(mapping.getSourceField().getUniqueFieldName());}).findFirst();
            if(fv.isPresent()){ 
                fieldValueMapping.add(fv.get());
            }

        });     
        documentMapper.setValueMappingObject(fieldValueMapping);        
        return documentMapper.getFieldMappingObject();
    }
    public List<Language> getLanguages(ISourceProcessor processor){     
        List<Language> listLanguages = new ArrayList<Language>();
        processor.getLanguages().stream().forEach(lang -> {
            Locale tmp = new Locale(lang);
            listLanguages.add(new Language(tmp.getLanguage(), tmp.getDisplayLanguage()));
        });
        return listLanguages;
    }
    
    public List<FieldValue> getSourceFieldValues(ISourceProcessor processor, String fieldName){     
        List<Field> fieldList = processor.getFields();
        List<FieldValue> possibleValues =  null;
        Field field = fieldList.stream().filter(n -> {
            return fieldName.equals(n.getFieldName());
        }).findFirst().get();
        if (field != null) {        
            possibleValues = field.getPossibleValues();
        }
        return possibleValues;      
    }
    
    public List<FieldValue> getDestinationFieldValues(IDestinationProcessor processor, String fieldName){
        List<Field> fieldList = processor.getFields();
        List<FieldValue> possibleValues = null;
        Field field = fieldList.stream().filter(n -> {
            return fieldName.equals(n.getFieldName());
        }).findFirst().get();
        if (field != null) {
            possibleValues = field.getPossibleValues();
        }
        return possibleValues;  
    }   
    
}
