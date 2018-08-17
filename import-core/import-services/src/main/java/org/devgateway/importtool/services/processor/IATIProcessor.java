package org.devgateway.importtool.services.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Class that will hold common code among all IATI processors
 */

public abstract class IATIProcessor implements ISourceProcessor {

    private Log log = LogFactory.getLog(getClass());

    public static final String DEFAULT_ID_FIELD = "iati-identifier";
    public static final String LAST_UPDATED_DATE = "last-updated-datetime";
    public static final String DEFAULT_PATH_API = "/results/iati-activities/iati-activity";
    public static String DEFAULT_GROUPING_FIELD = "reporting-org";

    protected String PROCESSOR_VERSION = "";

    // XML Document that will hold the entire imported file
    protected Document doc;
    protected String propertiesFile = "";

    private String defaultLanguage = "";
    private String defaultCurrency = "";
    private List<Field> filterFieldList = new ArrayList<Field>();
    private boolean fromDatastore = false;

    @Override
    public  void setFromDataStore(boolean fromDatastore) {
        this.fromDatastore = fromDatastore;
    }
    @Override
    public void setInput(Document input) {
            this.doc = input;
    }
    @Override
    public Document getDoc() {
        return doc;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public List<Field> getFilterFieldList() {
        return filterFieldList;
    }

    public void setFilterFieldList(List<Field> filterFieldList) {
        this.filterFieldList = filterFieldList;
    }

    protected void configureDefaults(){
        InputStream propsStream = this.getClass().getResourceAsStream(this.getPropertiesFile());
        Properties properties = new Properties();
        try {
            properties.load(propsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        defaultLanguage = properties.getProperty("default_language");
        defaultCurrency = properties.getProperty("default_currency");
    }

    protected NodeList getActivities() throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();

        String xpathExtractActivities = (fromDatastore ? "/result" : "" )  + "/iati-activities/iati-activity[";
        final StringBuilder query = new StringBuilder(xpathExtractActivities);
        Field countryField = this.getFields().stream().filter(f -> {
            return f.getType().equals(FieldType.RECIPIENT_COUNTRY);
        }).findFirst().get();

        Field countryFilters = filterFieldList.stream().filter(n -> {
            return countryField.getFieldName().equals(n.getFieldName());
        }).findFirst().get();

        if(countryFilters.getFilters().size() > 0){
            String selectedCountry  = countryFilters.getFilters().get(0);
            query.append("recipient-country[@code='"+ selectedCountry +"']");
        }


        this.getFields().forEach(field -> {
            if(field.getType().equals(FieldType.LIST)){
                Optional<Field> optFilter = filterFieldList.stream().filter(n -> {
                    return field.getFieldName().equals(n.getFieldName());
                }).findFirst();

                Field filter = optFilter.isPresent() ? optFilter.get() : null;
                if(filter != null && filter.getFilters().size() > 0){
                    if(!("/iati-activities/iati-activity[".equals(query.toString()))){
                        query.append(" and ");
                    }

                    //Some filters need relative paths
                    String fieldName = getFieldName(field.getFieldName());
                    query.append(fieldName + "[");
                    for (int i = 0;i < filter.getFilters().size(); i++) {
                        String value = filter.getFilters().get(i);
                        if(i > 0){
                            query.append(" or ");
                        }
                        query.append("@code='" + value + "'");
                    }
                    query.append("]");
                }
            }

        });
        if (fromDatastore) {
            if (!(xpathExtractActivities.equals(query.toString()))) {
                query.append("and " + getExtraQueryVersion());
            } else {
                query.append(getExtraQueryVersion());
            }
        }
        else{
            if (!(xpathExtractActivities.equals(query.toString()))) {
                query.append("]");

            }else{
                query.setLength(query.length() - 1);
            }

        }

        NodeList activities = (NodeList)xPath.compile(query.toString()).evaluate(this.getDoc(), XPathConstants.NODESET);
        return activities;
    }

    private String getFieldName(String fieldName) {
        String name = fieldName;
        switch(fieldName) {
            case "sector":
                name = "transaction/sector";
                break;
        }
        return name;
    }
    @Override
    public Boolean isValidInput() {
        NodeList nodeList = doc.getElementsByTagName("iati-activities");
        try {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node version = nodeList.item(i).getAttributes().getNamedItem("version");
                if (version == null)
                    continue;
                String ver = version.getNodeValue();
                if (ver.equalsIgnoreCase(PROCESSOR_VERSION)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Error validating IATI " + PROCESSOR_VERSION + " file");
        }

        return false;
    }
    public String getExtraQueryVersion(){
        return "@*[name()='iati-extra:version']='"+ PROCESSOR_VERSION +"']";
    }

}
