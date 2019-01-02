package org.devgateway.importtool.services.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.processor.helper.Constants;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.springframework.util.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

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
    protected String PROCESSOR_SUPER_VERSION;
    protected String codelistPath = "";
    protected String schemaPath = "";
    protected String activtySchemaName = "iati-activities-schema.xsd";
    protected String fieldsTooltipsLocation = null;
    protected String labelsTranslationsLocation = null;
    // XML Document that will hold the entire imported file
    protected Document doc;
    protected String propertiesFile = "";

    private String defaultLanguage = "";
    private String defaultCurrency = "";
    private List<Field> filterFieldList = new ArrayList<Field>();

    // Global Lists for fields and the filters
    private List<Field> fieldList = new ArrayList<Field>();
    private List<Language> filterLanguages = new ArrayList<Language>();


    private boolean fromDatastore = false;
    public final static Set<String> IMPLEMENTED_VERSIONS = new HashSet<>
            (Arrays.asList("1.01", "1.03", "1.04", "1.05",
            "2.01", "2.02","2.03"));
    //Variable that holds a MAP for tooltips and for fields translations
    protected final static Map<String,Map<String,Map<String,Map<String,String>> >>langPack = new HashMap();

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

    public boolean isFromDatastore() {
        return fromDatastore;
    }

    public void setFromDatastore(boolean fromDatastore) {
        this.fromDatastore = fromDatastore;
    }

    @Override
    public void setFilterLanguages(List<Language> filterLanguages) {
        this.filterLanguages = filterLanguages;
    }

    @Override
    public List<Field> getFields() {
        return fieldList;
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

                   if ("sector".equals(field.getFieldName())) {                    	
                    	query.append(" (");                       
                        query.append(buildFieldFilter("transaction/sector", filter));                        
                        query.append(" | ");                        
                        query.append(buildFieldFilter("sector", filter));                          
                        query.append(") ");                        
                    } else {
                    	query.append(buildFieldFilter(field.getFieldName(), filter));                    	
                    }
                    
                }
            }

        });
        if (fromDatastore) {
            if (!(xpathExtractActivities.equals(query.toString()))) {
                query.append(" and " + getExtraQueryVersion());
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

    private String buildFieldFilter(String fieldName, Field filter) {
    	final StringBuilder query = new StringBuilder("");
        query.append(fieldName + "[");
        for (int i = 0;i < filter.getFilters().size(); i++) {
            String value = filter.getFilters().get(i);
            if(i > 0){
                query.append(" or ");
            }
            query.append("@code='" + value + "'");
        }
        query.append("]");
        return query.toString();
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

    protected Document getDocument(String fileName) throws ParserConfigurationException, SAXException, IOException {
        InputStream is = this.getClass().getResourceAsStream(fileName);
        if(is == null){
            System.out.println("this field name is null:" + fileName);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    public List<String>  buildTooltipsFields() {
        try {
            List<String> missing = new ArrayList<>();
            String descriptionXPath = "/*[name()='xsd:schema']/*[name()='xsd:element'][@name='%s']/*[name()='xsd:annotation']/*[name()" +
                    "='xsd:documentation']";
            Properties prop = new Properties();
            URL url = this.getClass().getResource(this.fieldsTooltipsLocation + "_en.properties");
            File fileObject = new File(url.toURI());
            FileOutputStream out = new FileOutputStream(fileObject);
            Document doc = getDocument(this.schemaPath + this.activtySchemaName);
            this.fieldList.stream().forEach(field -> {
                String fieldXPath = String.format(descriptionXPath, field.getFieldName());
                NodeList descriptionList = getNodeListFromXpath(doc, fieldXPath);
                if (descriptionList.getLength() == 1) {
                    prop.setProperty(field.getFieldName(), descriptionList.item(0).getFirstChild().getNodeValue());
                } else {
                    missing.add(field.getFieldName());
                }
            });
            prop.store(out, null);

            return missing;

        } catch (Exception ex) {
            log.error("Cannot generate tooltips", ex);
            return null;
        }
    }

    public NodeList getNodeListFromXpath(Document xDoc, String xPathExpresion)  {

        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            Node node= (Node)xPath.compile(xPathExpresion).evaluate(xDoc,XPathConstants.NODE);
            return (NodeList) xPath.compile(xPathExpresion).evaluate(xDoc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.error("Cannot evaluete xpath", e);
            return null;
        }
    }
    public Map<String, String> getLabelsForField(String field){
        return getLabelsForVersion(this.PROCESSOR_SUPER_VERSION).get(field);
    }
    public Map<String, String> getTooltipForField(String field){
        return getTooltipForVersion(this.PROCESSOR_VERSION).get(field);
    }

    @Override
    public List<Language> getFilterLanguages() {
        if(this.filterLanguages.size() == 0){
            List<Language> listLanguages = new ArrayList<Language>();
            this.getLanguages().stream().forEach(lang -> {
                Locale tmp = new Locale(lang);
                listLanguages.add(new Language(tmp.getLanguage(), tmp.getDisplayLanguage()));
            });
            this.setFilterLanguages(listLanguages);
        }
        return this.filterLanguages;
    }

    public  Map<String, Map<String, String>> getTooltipForVersion(String version) {
        return getLangPackForTypeAndVersion(version, Constants.LANG_PACK_TOOLTIPS);
    }
    public  Map<String, Map<String, String>> getLabelsForVersion(String version) {
        return getLangPackForTypeAndVersion(version, Constants.LANG_PACK_LABELS);
    }

    private Map<String, Map<String, String>> getLangPackForTypeAndVersion(String version, String langPackType) {

        Map<String,Map<String,Map<String,String>> > descriptionTooltipsMap = getLangPack(langPackType);

        if (descriptionTooltipsMap.get(version) == null) {
            //map that will hold the translation pack for a version
            Map<String, Map<String, String>> mapForVersion = new HashMap<>();
            Constants.SUPPORTED_LOCALES.stream().forEach(lang -> {
                try {
                    Properties properties = new Properties();
                    properties.load(new InputStreamReader(this.getClass().getResource(getPropertieFieldName(lang,
                            langPackType))
                            .openStream(), Charset.forName("UTF-8")));
                    properties.stringPropertyNames().stream().forEach(property -> {
                        if (mapForVersion.get(property) == null) {
                            mapForVersion.put(property, new HashMap<String, String>() {{
                                put(lang, properties.getProperty(property));
                            }});
                        } else {
                            mapForVersion.get(property).put(lang, properties.getProperty(property));
                        }
                    });
                } catch (IOException ex) {
                    //we need to properly handle exceptions but we need to do it on scope of the refactoring
                    //so we properly inform the user of the problem. T
                    log.error("Cannot process properties file " + this.fieldsTooltipsLocation +
                            "_" + lang + ".properties");
                }
            });
            descriptionTooltipsMap.put(version, mapForVersion);
        }
        return descriptionTooltipsMap.get(version);
    }

    public Map<String,Map<String,Map<String,String>> > getLangPack(String langPackType) {
        if (langPack.get(langPackType) == null) {
            langPack.put(langPackType, new HashMap<>());
        }
        return langPack.get(langPackType);
    }

    private String getPropertieFieldName(String lang,String langPackType) {
        String fileName;
        switch (langPackType) {
            case Constants.LANG_PACK_TOOLTIPS:
                fileName = this.fieldsTooltipsLocation;
                break;
            case Constants.LANG_PACK_LABELS:
                fileName = this.labelsTranslationsLocation;
                break;
            default:
                throw new UnsupportedOperationException("LangPackType not supported");

        }
        return fileName + "_" + lang + ".properties";
    }

    protected List<String> extractLanguage(NodeList elementsByTagName) {
        List<String> list = new ArrayList<String>();
        try {
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                Node langAttr = elementsByTagName.item(i).getAttributes().getNamedItem("xml:lang");
                if (langAttr == null)
                    continue;
                String lang = langAttr.getNodeValue().trim().toLowerCase();
                if (!list.contains(lang)) {
                    list.add(lang);
                }
            }
        } catch (Exception e) {
            log.error("Error extracting languages", e);
        }
        return list;
    }
    protected void processOrganizationElementType(InternalDocument document, Element element, Field field) {
        NodeList fieldNodeList;
        fieldNodeList = element.getElementsByTagName(field.getFieldName());
        if (fieldNodeList.getLength() > 0) {
            for (int j = 0; j < fieldNodeList.getLength(); j++) {
                Element fieldElement = (Element) fieldNodeList.item(j);
                if (fieldElement.getAttribute("role").equals(field.getSubType()) || fieldElement.getAttribute("role").equals(field.getSubTypeCode())) {
                    final String stringOrgValue = getStringOrgValue(fieldElement);

                    final String ref = fieldElement.getAttribute("ref");

                    if ((stringOrgValue != null && !stringOrgValue.trim().isEmpty())
                            || (ref != null && !ref.trim().isEmpty())) {
                        Map<String, String> orgFields = new HashMap<String, String>();
                        orgFields.put("value", stringOrgValue);
                        orgFields.put("role", field.getSubType());
                        orgFields.put("ref", ref);
                        orgFields.put("type", fieldElement.getAttribute("type"));
                        FieldValue fv = new FieldValue();
                        if (stringOrgValue != null && !stringOrgValue.isEmpty()) {
                            fv.setCode(stringOrgValue);
                            fv.setValue(stringOrgValue);
                        } else {
                            fv.setCode(ref);
                            fv.setValue(ref);
                        }
                        fv.setSelected(true);
                        int index = field.getPossibleValues() == null ? 0
                                : field.getPossibleValues().size();
                        fv.setIndex(index);
                        if (field.getPossibleValues() == null) {
                            field.setPossibleValues(new ArrayList<FieldValue>());
                        }
                        if (!field.getPossibleValues().stream().anyMatch(n -> {
                            return n.getCode().equals(stringOrgValue);
                        })) {
                            field.getPossibleValues().add(fv);
                        }

                        document.addOrganizationField(field.getFieldName() + "_" + field.getSubType() + "_"
                                + DigestUtils.md5DigestAsHex(fv.getValue().getBytes()), orgFields);

                    }
                }
            }
        }
    }
    protected  void processRecipientCountryElementType(InternalDocument document, Element element, Field field) {
        NodeList fieldNodeList;
        Field filtersField = getFilterFieldList().stream().filter(n -> {
            return field.getFieldName().equals(n.getFieldName());
        }).findFirst().get();

        fieldNodeList = element.getElementsByTagName(field.getFieldName());
        List<FieldValue> recipients = new ArrayList<>();
        for (int j = 0; j < fieldNodeList.getLength(); j++) {
            Element fieldElement = (Element) fieldNodeList.item(j);
            FieldValue recipient = new FieldValue();
            String code = fieldElement.getAttribute("code");
            boolean includeCountry = includedByFilter(filtersField.getFilters(), code);
            if(includeCountry){
                recipient.setCode(code);
                Optional<FieldValue> fieldValue = field.getPossibleValues().stream().filter(f -> f.getCode().equals(code)).findFirst();
                if(fieldValue.isPresent()){
                    recipient.setValue(fieldValue.get().getValue());
                }
                recipient.setPercentage(fieldElement.getAttribute("percentage"));
                recipients.add(recipient);
            }
        }
        document.addRecepientCountryFields(field.getFieldName(), recipients);
    }

    protected Boolean includedByFilter(List<String> filters, String codeValue) {
        if (filters.size() == 0)
            return true;

        for (String value : filters) {
            if (value.equals(codeValue)) {
                return true;
            }
        }
        return false;
    }
    protected abstract String getStringOrgValue(Element fieldElement);

}
