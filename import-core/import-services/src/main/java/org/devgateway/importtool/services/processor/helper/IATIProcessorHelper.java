package org.devgateway.importtool.services.processor.helper;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IATIProcessorHelper {

    // Map that holds information about how the field names map to code lists
    public static Map<String, String> mappingNameFile = new HashMap<String, String>();
    static {
        mappingNameFile.put("activity-status", "ActivityStatus");
        mappingNameFile.put("activity-scope", "ActivityScope");
        mappingNameFile.put("collaboration-type", "CollaborationType");
        mappingNameFile.put("recipient-country", "Country");
        mappingNameFile.put("recipient-region", "Region");
        mappingNameFile.put("default-aid-type", "AidType");
        mappingNameFile.put("default-finance-type", "FinanceType");
        mappingNameFile.put("default-flow-type", "FlowType");
        mappingNameFile.put("default-tied-status", "TiedStatus");
        mappingNameFile.put("policy-marker", "PolicyMarker");
        mappingNameFile.put("sector", "Sector");
    }
    public static void processListElementType(InternalDocument document, Element element, Field field) {
        NodeList fieldNodeList;
        if (field.isMultiple()) {
            fieldNodeList = element.getElementsByTagName(field.getFieldName());
            List<String> codes = new ArrayList<String>();
            for (int j = 0; j < fieldNodeList.getLength(); j++) {
                Element fieldElement = (Element) fieldNodeList.item(j);
                String code = fieldElement.getAttribute("code");
                if(!code.isEmpty()){
                    codes.add(code);
                    Optional<FieldValue> foundfv = field.getPossibleValues().stream().filter(n -> {return n.getCode().equals(code);}).findFirst();
                    FieldValue fv  = foundfv.isPresent() ? foundfv.get() : null;
                    if(fv != null && fv.isSelected() != true){
                        fv.setSelected(true);
                    }
                }
            }
            if(!codes.isEmpty()){
                String[] codeValues = codes.stream().toArray(String[]::new);
                document.addStringMultiField(field.getFieldName(), codeValues);
            }
        } else {
            fieldNodeList = element.getElementsByTagName(field.getFieldName());
            if (fieldNodeList.getLength() > 0 && fieldNodeList.getLength() == 1) {
                Element fieldElement = (Element) fieldNodeList.item(0);
                String codeValue = fieldElement.getAttribute("code");
                if(!codeValue.isEmpty()){
                    Optional<FieldValue> foundfv = field.getPossibleValues().stream().filter( n -> {
                        return n.getCode().equals(codeValue);
                    }).findFirst();
                    FieldValue fv  = foundfv.isPresent() ? foundfv.get() : null;
                    if(fv != null && fv.isSelected() != true){
                        fv.setSelected(true);
                    }
                    document.addStringField(field.getFieldName(), codeValue);
                }
            }
        }
    }
    public static void processStringElementType(InternalDocument document, Element element, Field field) {
        String stringValue = getStringFromElement(element, field.getFieldName());
        document.addStringField(field.getFieldName(), stringValue);
    }
    public static String getStringFromElement(Element element, String field){
        return getStringFromElement(element, field, null);
    }
    public static String getStringFromElement(Element element, String field,String attribute) {
        NodeList fieldNodeList;
        String returnValue = "";
        fieldNodeList = element.getElementsByTagName(field);
        if (fieldNodeList.getLength() > 0 && fieldNodeList.getLength() == 1) {
            Element fieldElement = (Element) fieldNodeList.item(0);
            if (attribute == null) { //we are getting the first element text value
                if (fieldElement.getChildNodes().getLength() == 1) {
                    returnValue = fieldElement.getChildNodes().item(0).getNodeValue();
                } else {
                    returnValue = "";
                }
            } else {
                //we are getting the attribute value
                returnValue = fieldElement.getAttribute(attribute);
            }
        }
        return returnValue;
    }

}
