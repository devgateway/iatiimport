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
