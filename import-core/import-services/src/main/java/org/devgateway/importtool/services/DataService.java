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
                documentMapper.setValueMappingObject(new ArrayList<>());
            }
            documentMapper.getFieldMappingObject().stream().forEach(fieldMapping -> {
                addFieldValueMapping(documentMapper, fieldMapping, valuesInSelectedSourceProjects);
            });
        } else {
            documentMapper.getFieldMappingObject().stream().forEach(fieldMapping -> {
                documentMapper.getValueMappingObject()
                        .removeIf(fv -> !fieldValueMappingMatchWithFieldMapping(fv, fieldMapping));

                Boolean alreadyInserted = documentMapper.getValueMappingObject().stream()
                        .anyMatch(n -> isFieldValueOfFieldMapping(n, fieldMapping));

                if (!alreadyInserted) {
                    addFieldValueMapping(documentMapper, fieldMapping, valuesInSelectedSourceProjects);
                }
            });
        }
        
        return documentMapper.getValueMappingObject();
    }

    /**
     * Detect if the field value's source field and destination field matches with the field mapping's field
     *
     * @param fv
     * @param fieldMapping
     * @return
     */
    private boolean fieldValueMappingMatchWithFieldMapping(FieldValueMapping fv, FieldMapping fieldMapping) {
        if (fv.getSourceField().getUniqueFieldName().equals(fieldMapping.getSourceField().getUniqueFieldName())) {
            return fv.getDestinationField().getUniqueFieldName().equals(fieldMapping.getDestinationField().getUniqueFieldName());
        }

        return true;
    }

    /**
     * Detect if the value mapping's fields are the same of field mapping's fields
     *
     * @param fv
     * @param fieldMapping
     * @return
     */
    private boolean isFieldValueOfFieldMapping(FieldValueMapping fv, FieldMapping fieldMapping) {
        return fv.getSourceField().getUniqueFieldName().equals(fieldMapping.getSourceField().getUniqueFieldName())
                && fv.getDestinationField().getUniqueFieldName().equals(fieldMapping.getDestinationField().getUniqueFieldName());
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
