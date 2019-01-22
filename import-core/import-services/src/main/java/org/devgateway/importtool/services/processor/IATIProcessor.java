package org.devgateway.importtool.services.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.processor.helper.*;
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
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.devgateway.importtool.services.processor.helper.FieldType.TRANSACTION;
import static org.devgateway.importtool.services.processor.helper.IATIProcessorHelper.getStringFromElement;

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

    private ActionStatus actionStatus;


    private boolean fromDatastore = false;
    public final static Set<String> IMPLEMENTED_VERSIONS = new HashSet<>
            (Arrays.asList("1.01", "1.03", "1.04", "1.05",
                    "2.01", "2.02", "2.03"));
    //Variable that holds a MAP for tooltips and for fields translations
    protected final static Map<String, Map<String, Map<String, Map<String, String>>>> langPack = new HashMap();

    private static final String ISO_DATE = "yyyy-MM-dd";
    private static final String ISO_ATTRIBUTE = "setFromDataStore";
    private SimpleDateFormat df = new SimpleDateFormat(ISO_DATE);


    @Override
    public void setFromDataStore(boolean fromDatastore) {
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

    @Override
    public void setFilterLanguages(List<Language> filterLanguages) {
        this.filterLanguages = filterLanguages;
    }

    @Override
    public List<Field> getFields() {
        return fieldList;
    }

    @Override
    public ActionStatus getActionStatus() {
        return actionStatus;
    }

    @Override
    public void setActionStatus(ActionStatus actionStatus) {
        this.actionStatus = actionStatus;
    }

    public String getCodelistPath() {
        return codelistPath;
    }

    public void setCodelistPath(String codelistPath) {
        this.codelistPath = codelistPath;
    }

    @Override
    public void setInput(InputStream input) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.doc = builder.parse(input);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("IOException Parsing Source File: " + e);
        }
    }
    protected void instantiateStaticFields() {

        // Text fields
        getFields().add(new Field("IATI Identifier", "iati-identifier", FieldType.STRING,
                false, getTooltipForField("iati-identifier"),getLabelsForField("iati-identifier")));

        getFields().add(new Field("Title", "title", FieldType.MULTILANG_STRING, false,
                getTooltipForField("title"),getLabelsForField("title")));

        getFields().add(new Field("Description", "description", FieldType.MULTILANG_STRING,
                true, getTooltipForField("description"), getLabelsForField("description")));

        // Code Lists
        Field activityStatus = new Field("Activity Status", "activity-status", FieldType.LIST,
                true, getTooltipForField("activity-status"), getLabelsForField("activity-status"));
        activityStatus.setPossibleValues(getCodeListValues("activity-status"));
        getFields().add(activityStatus);
        getFilterFieldList().add(activityStatus);

        Field activityScope = new Field("Activity Scope", "activity-scope", FieldType.LIST,
                true, getTooltipForField("activity-scope"), getLabelsForField("activity-scope"));
        activityScope.setPossibleValues(getCodeListValues("activity-scope"));
        getFields().add(activityScope);
        getFilterFieldList().add(activityScope);

        Field aidType = new Field("Aid Type", "default-aid-type", FieldType.LIST, true,
                getTooltipForField("default-aid-type"), getLabelsForField("default-aid-type"));
        aidType.setPossibleValues(getCodeListValues("default-aid-type"));
        getFields().add(aidType);
        getFilterFieldList().add(aidType);

        Field financeType = new Field("Finance Type", "default-finance-type", FieldType.LIST,
                true, getTooltipForField("default-finance-type"), getLabelsForField("default-finance-type"));
        financeType.setPossibleValues(getCodeListValues("default-finance-type"));
        getFields().add(financeType);
        getFilterFieldList().add(financeType);

        Field flowType = new Field("Flow Type", "default-flow-type", FieldType.LIST,
                true, getTooltipForField("default-flow-type"), getLabelsForField("default-flow-type"));
        flowType.setPossibleValues(getCodeListValues("default-flow-type"));
        getFields().add(flowType);
        getFilterFieldList().add(flowType);

        Field tiedStatus = new Field("Tied Status", "default-tied-status", FieldType.LIST,
                true, getTooltipForField("default-tied-status"), getLabelsForField("default-tied-status"));
        tiedStatus.setPossibleValues(getCodeListValues("default-tied-status"));
        getFields().add(tiedStatus);
        getFilterFieldList().add(tiedStatus);

        Field policyMarker = new Field("PolicyMarker", "policy-marker", FieldType.LIST,
                true, getTooltipForField("policy-marker"), getLabelsForField("policy-marker"));
        policyMarker.setPossibleValues(getCodeListValues("policy-marker"));
        policyMarker.setMultiple(true);
        getFields().add(policyMarker);
        getFilterFieldList().add(policyMarker);

        Field recipientCountry = new Field("Recipient Country", "recipient-country",
                FieldType.RECIPIENT_COUNTRY, true, getTooltipForField("recipient-country"),
                getLabelsForField("recipient-country"));
        recipientCountry.setPossibleValues(getCodeListValues("recipient-country"));
        recipientCountry.setExclusive(true);
        recipientCountry.setFilterRequired(true);
        getFields().add(recipientCountry);
        getFilterFieldList().add(recipientCountry);

        Field sector = new Field("Sector", "sector", FieldType.LIST, true,
                getTooltipForField("sector"), getLabelsForField("sector"));
        sector.setPossibleValues(getCodeListValues("sector"));
        sector.setMultiple(true);
        sector.setPercentage(true);
        getFields().add(sector);
        getFilterFieldList().add(sector);

        Field location = new Field("Location", "location", FieldType.LOCATION, true,
                getTooltipForField("location"), getLabelsForField("location"));
        location.setPossibleValues(new ArrayList<FieldValue>());
        location.setMultiple(true);
        location.setPercentage(true);
        getFields().add(location);

        // Dates
        Field activityDateStartPlanned = new Field("Activity Date Start Planned", "activity-date"
                , FieldType.DATE, true,getTooltipForField("activity-date_start-planned"), getLabelsForField("activity" +
                "-date_start-planned"));
        activityDateStartPlanned.setSubType("start-planned");
        getFields().add(activityDateStartPlanned);

        Field activityDateEndPlanned = new Field("Activity Date End Planned", "activity-date",
                FieldType.DATE, true, getTooltipForField("activity-date_end-planned"),
                getLabelsForField("activity-date_end-planned"));
        getFields().add(activityDateEndPlanned);
        activityDateEndPlanned.setSubType("end-planned");

        Field activityDateStartActual = new Field("Activity Date Start Actual", "activity-date",
                FieldType.DATE, true, getTooltipForField("activity-date_start-actual"), getLabelsForField("activity" +
                "-date_start-actual"));
        activityDateStartActual.setSubType("start-actual");
        getFields().add(activityDateStartActual);

        Field activityDateEndActual = new Field("Activity Date End Actual", "activity-date",
                FieldType.DATE, true, getTooltipForField("activity-date_end-actual"), getLabelsForField("activity" +
                "-date_end-actual"));
        getFields().add(activityDateEndActual);
        activityDateEndActual.setSubType("end-actual");

        // Transaction Fields
        Field commitments = new Field("Commitments", "transaction", FieldType.TRANSACTION,
                true, getTooltipForField("transaction_C"), getLabelsForField("transaction_C"));
        commitments.setSubType("C");
        commitments.setSubTypeCode("2");
        getFields().add(commitments);

        Field disbursements = new Field("Disbursements", "transaction", FieldType.TRANSACTION,
                true, getTooltipForField("transaction_D"), getLabelsForField("transaction_D"));
        disbursements.setSubType("D");
        disbursements.setSubTypeCode("3");
        getFields().add(disbursements);

        // Organization Fields
        Field participatingOrg = new Field(Constants.FUNDING_ORG_DISPLAY_NAME, "participating-org",
                FieldType.ORGANIZATION, true, getTooltipForField("participating-org_"
                + Constants.ORG_ROLE_FUNDING), getLabelsForField("participating-org_"
                + Constants.ORG_ROLE_FUNDING));
        participatingOrg.setSubType(Constants.ORG_ROLE_FUNDING);
        participatingOrg.setSubTypeCode(Constants.ORG_ROLE_FUNDING_CODE);
        getFields().add(participatingOrg);

        Field accountableOrg = new Field(Constants.ACCOUNTABLE_ORG_DISPLAY_NAME, "participating-org",
                FieldType.ORGANIZATION, true, getTooltipForField("participating-org_"
                + Constants.ORG_ROLE_ACCOUNTABLE), getLabelsForField("participating-org_"
                + Constants.ORG_ROLE_ACCOUNTABLE));
        accountableOrg.setSubTypeCode(Constants.ORG_ROLE_ACCOUNTABLE_CODE);
        accountableOrg.setSubType(Constants.ORG_ROLE_ACCOUNTABLE);
        getFields().add(accountableOrg);

        Field extendingOrg = new Field(Constants.EXTENDING_ORG_DISPLAY_NAME, "participating-org",
                FieldType.ORGANIZATION, true, getTooltipForField("participating-org_"
                + Constants.ORG_ROLE_EXTENDING), getLabelsForField("participating-org_"
                + Constants.ORG_ROLE_EXTENDING));
        extendingOrg.setSubTypeCode(Constants.ORG_ROLE_EXTENDING_CODE);
        extendingOrg.setSubType(Constants.ORG_ROLE_EXTENDING);
        getFields().add(extendingOrg);

        Field implementingOrg = new Field(Constants.IMPLEMENTING_ORG_DISPLAY_NAME, "participating-org",
                FieldType.ORGANIZATION, true, getTooltipForField("participating-org_"
                + Constants.ORG_ROLE_IMPLEMENTING), getLabelsForField("participating-org_"
                + Constants.ORG_ROLE_IMPLEMENTING));
        implementingOrg.setSubTypeCode(Constants.ORG_ROLE_IMPLEMENTING_CODE);
        implementingOrg.setSubType(Constants.ORG_ROLE_IMPLEMENTING);
        getFields().add(implementingOrg);

        // Provider Organization, within Transactions
        Field providerOrg = new ProviderOganizationField(Constants.PROVIDER_ORG_DISPLAY_NAME, "provider-org",
                FieldType.ORGANIZATION, false, getTooltipForField("provider-org"),
                getLabelsForField("provider-org"));
        providerOrg.setSubType("Provider");
        getFields().add(providerOrg);
        getFilterFieldList().add(providerOrg);
    }

    private List<FieldValue> getCodeListValues(String codeListName) {
        return getCodeListValues(codeListName, false);
    }

    protected List<FieldValue> getCodeListValues(String codeListName, Boolean concatenate) {
        String standardFieldName = IATIProcessorHelper.mappingNameFile.get(codeListName);
        List<FieldValue> possibleValues = new ArrayList<>();
        try {
            Document doc = getDocument(this.getCodelistPath() + standardFieldName + ".xml");
            NodeList nodeList = getNodeListForCodeListValues(doc, standardFieldName);
            int index = 0;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Element codeElement = (Element) element.getElementsByTagName("code").item(0);
                    String code = codeElement.getChildNodes().item(0).getNodeValue();
                    Element nameElement = (Element) element.getElementsByTagName("name").item(0);

                    String name = extractNameElementForCodeListValues(nameElement);

                    FieldValue fv = new FieldValue();
                    fv.setIndex(index++);
                    fv.setCode(code);
                    if (concatenate) {
                        fv.setValue(code + " - " + name);
                    } else {
                        fv.setValue(name);
                    }

                    possibleValues.add(fv);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("IOException Parsing Source File: " + e);
        }
        return possibleValues;
    }
    protected abstract NodeList getNodeListForCodeListValues(Document doc, String  standardFieldName);
    protected abstract String extractNameElementForCodeListValues(Element nameElement) ;
    protected void configureDefaults() {
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

        String xpathExtractActivities = (fromDatastore ? "/result" : "") + "/iati-activities/iati-activity[";
        final StringBuilder query = new StringBuilder(xpathExtractActivities);
        Field countryField = this.getFields().stream().filter(f -> {
            return f.getType().equals(FieldType.RECIPIENT_COUNTRY);
        }).findFirst().get();

        Field countryFilters = filterFieldList.stream().filter(n -> {
            return countryField.getFieldName().equals(n.getFieldName());
        }).findFirst().get();

        if (countryFilters.getFilters().size() > 0) {
            String selectedCountry = countryFilters.getFilters().get(0);
            query.append("recipient-country[@code='" + selectedCountry + "']");
        }


        this.getFields().forEach(field -> {
            if (field.getType().equals(FieldType.LIST)) {
                Optional<Field> optFilter = filterFieldList.stream().filter(n -> {
                    return field.getFieldName().equals(n.getFieldName());
                }).findFirst();

                Field filter = optFilter.isPresent() ? optFilter.get() : null;
                if (filter != null && filter.getFilters().size() > 0) {
                    if (!("/iati-activities/iati-activity[".equals(query.toString()))) {
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
        } else {
            if (!(xpathExtractActivities.equals(query.toString()))) {
                query.append("]");

            } else {
                query.setLength(query.length() - 1);
            }

        }

        NodeList activities = (NodeList) xPath.compile(query.toString()).evaluate(this.getDoc(), XPathConstants.NODESET);
        return activities;
    }

    private String buildFieldFilter(String fieldName, Field filter) {
        final StringBuilder query = new StringBuilder("");
        query.append(fieldName + "[");
        for (int i = 0; i < filter.getFilters().size(); i++) {
            String value = filter.getFilters().get(i);
            if (i > 0) {
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

    public String getExtraQueryVersion() {
        return "@*[name()='iati-extra:version']='" + PROCESSOR_VERSION + "']";
    }

    protected Document getDocument(String fileName) throws ParserConfigurationException, SAXException, IOException {
        InputStream is = this.getClass().getResourceAsStream(fileName);
        if (is == null) {
            log.error("this field name is null:" + fileName);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    public List<String> buildTooltipsFields() {
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

    public NodeList getNodeListFromXpath(Document xDoc, String xPathExpresion) {

        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            return (NodeList) xPath.compile(xPathExpresion).evaluate(xDoc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.error("Cannot evaluete xpath", e);
            return null;
        }
    }

    public Map<String, String> getLabelsForField(String field) {
        return getLabelsForVersion(this.PROCESSOR_SUPER_VERSION).get(field);
    }

    public Map<String, String> getTooltipForField(String field) {
        return getTooltipForVersion(this.PROCESSOR_VERSION).get(field);
    }

    @Override
    public List<Language> getFilterLanguages() {
        if (this.filterLanguages.size() == 0) {
            List<Language> listLanguages = new ArrayList<Language>();
            this.getLanguages().stream().forEach(lang -> {
                Locale tmp = new Locale(lang);
                listLanguages.add(new Language(tmp.getLanguage(), tmp.getDisplayLanguage()));
            });
            this.setFilterLanguages(listLanguages);
        }
        return this.filterLanguages;
    }

    public Map<String, Map<String, String>> getTooltipForVersion(String version) {
        return getLangPackForTypeAndVersion(version, Constants.LANG_PACK_TOOLTIPS);
    }

    public Map<String, Map<String, String>> getLabelsForVersion(String version) {
        return getLangPackForTypeAndVersion(version, Constants.LANG_PACK_LABELS);
    }

    private Map<String, Map<String, String>> getLangPackForTypeAndVersion(String version, String langPackType) {

        Map<String, Map<String, Map<String, String>>> descriptionTooltipsMap = getLangPack(langPackType);

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

    public Map<String, Map<String, Map<String, String>>> getLangPack(String langPackType) {
        if (langPack.get(langPackType) == null) {
            langPack.put(langPackType, new HashMap<>());
        }
        return langPack.get(langPackType);
    }

    private String getPropertieFieldName(String lang, String langPackType) {
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
    protected List<InternalDocument> extractDocuments(Document doc) throws Exception {
        // Extract global values
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = getActivities();
        List<InternalDocument> list = new ArrayList<>();
        actionStatus.setTotal(Long.valueOf(nodeList.getLength()));
        this.clearUsedValues();
        for (int i = 0; i < nodeList.getLength(); i++) {
            actionStatus.incrementProcessed();
            InternalDocument document = new InternalDocument();

            Element iatiActivity = (Element) nodeList.item(i);
            //we set the grouping criteria
            document.setGrouping(getStringFromElement(iatiActivity, DEFAULT_GROUPING_FIELD, "ref"));

            String currency = !("".equals(iatiActivity.getAttribute("default-currency"))) ? iatiActivity.getAttribute
                    ("default-currency") : this.getDefaultCurrency();
            document.addStringField("default-currency", currency);
            String defaultLanguageCode = !("".equals(iatiActivity.getAttribute("xml:lang"))) ? iatiActivity.getAttribute
                    ("xml:lang") : this.getDefaultLanguage();

            String xPathParticipatingOrg = "participating-org[@role=1]";
            NodeList activityparticipatingOrgs = (NodeList) xPath.evaluate(xPathParticipatingOrg,
                    iatiActivity, XPathConstants.NODESET);
            Element activityParticipatingOrgrNode = null;
            if (activityparticipatingOrgs.getLength() > 0) {
                activityParticipatingOrgrNode = (Element) activityparticipatingOrgs.item(0);
            }

            processLocationElementType(document, iatiActivity);
            processListElementType(document, iatiActivity);
            processRecipientCountryElementType(document, iatiActivity);
            processStringElementType(document, iatiActivity);
            processOrganizationElementType(document, iatiActivity);
            processMultiLangElementType(xPath, document, iatiActivity, defaultLanguageCode);
            processTransactionElementType(document, iatiActivity, activityParticipatingOrgrNode);
            processDateElementType(document, iatiActivity);

            list.add(document);
        }
        return list;
    }
    protected abstract void processMultiLangElementType(XPath xPath, InternalDocument document, Element element,
                                               String defaultLanguageCode) ;

    protected void processStringElementType(InternalDocument document, Element element) {
        processForEachFilteredByType(field -> {
            String stringValue = getStringFromElement(element, field.getFieldName());
            document.addStringField(field.getFieldName(), stringValue);
        }, FieldType.STRING);
    }

    protected void processForEachFilteredByType(Consumer<Field> action, FieldType fieldType) {
        getFields().stream().filter(fieldFilter ->
                fieldFilter.getType().equals(fieldType)
        ).forEach(action);
    }

    protected void processListElementType(InternalDocument document, Element element) {
        Consumer<Field> listConsumer = field -> {
            NodeList fieldNodeList;
            if (field.isMultiple()) {
                fieldNodeList = element.getElementsByTagName(field.getFieldName());
                List<String> codes = new ArrayList<String>();
                for (int j = 0; j < fieldNodeList.getLength(); j++) {
                    Element fieldElement = (Element) fieldNodeList.item(j);
                    String code = fieldElement.getAttribute("code");
                    if (!code.isEmpty()) {
                        codes.add(code);
                        Optional<FieldValue> foundfv = field.getPossibleValues().stream().filter(n -> {
                            return n.getCode().equals(code);
                        }).findFirst();
                        FieldValue fv = foundfv.isPresent() ? foundfv.get() : null;
                        if (fv != null && fv.isSelected() != true) {
                            fv.setSelected(true);
                        }
                    }
                }
                if (!codes.isEmpty()) {
                    String[] codeValues = codes.stream().toArray(String[]::new);
                    document.addStringMultiField(field.getFieldName(), codeValues);
                }
            } else {
                fieldNodeList = element.getElementsByTagName(field.getFieldName());
                if (fieldNodeList.getLength() > 0 && fieldNodeList.getLength() == 1) {
                    Element fieldElement = (Element) fieldNodeList.item(0);
                    String codeValue = fieldElement.getAttribute("code");
                    if (!codeValue.isEmpty()) {
                        Optional<FieldValue> foundfv = field.getPossibleValues().stream().filter(n -> {
                            return n.getCode().equals(codeValue);
                        }).findFirst();
                        FieldValue fv = foundfv.isPresent() ? foundfv.get() : null;
                        if (fv != null && fv.isSelected() != true) {
                            fv.setSelected(true);
                        }
                        document.addStringField(field.getFieldName(), codeValue);
                    }
                }
            }
        };
        processForEachFilteredByType(listConsumer, FieldType.LIST);
    }

    protected void processOrganizationElementType(InternalDocument document, Element element) {

        Consumer<Field> organizationConsumer = field -> {
            NodeList fieldNodeList;
            fieldNodeList = element.getElementsByTagName(field.getFieldName());
            if (fieldNodeList.getLength() > 0) {
                for (int j = 0; j < fieldNodeList.getLength(); j++) {
                    Element fieldElement = (Element) fieldNodeList.item(j);
                    //this can be generic not to process all the organization sub types and process them at the same moment
                    if (fieldElement.getAttribute("role").equals(field.getSubType())
                            || fieldElement.getAttribute("role").equals(field.getSubTypeCode())) {
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
        };
        processForEachFilteredByType(organizationConsumer, FieldType.ORGANIZATION);
    }

    protected void processRecipientCountryElementType(InternalDocument document, Element element) {

        Consumer<Field> recipientCountry = field -> {
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
                if (includeCountry) {
                    recipient.setCode(code);
                    Optional<FieldValue> fieldValue = field.getPossibleValues().stream().filter(f -> f.getCode().equals(code)).findFirst();
                    if (fieldValue.isPresent()) {
                        recipient.setValue(fieldValue.get().getValue());
                    }
                    recipient.setPercentage(fieldElement.getAttribute("percentage"));
                    recipients.add(recipient);
                }
            }
            document.addRecepientCountryFields(field.getFieldName(), recipients);
        };
        processForEachFilteredByType(recipientCountry, FieldType.RECIPIENT_COUNTRY);
    }

    protected void processLocationElementType(InternalDocument document, Element element) {
        Consumer<Field> locationConsumer = field -> {
            NodeList fieldNodeList = element.getElementsByTagName(field.getFieldName());
            List<String> codesLocation = new ArrayList<>();
            for (int j = 0; j < fieldNodeList.getLength(); j++) {
                Element fieldElement = (Element) fieldNodeList.item(j);
                String name = getNameFromElement(fieldElement);

                if (!StringUtils.isBlank(name)) {
                    codesLocation.add(name);
                    FieldValue fv = new FieldValue();
                    if (name != null && !name.isEmpty()) {
                        fv.setCode(name);
                        fv.setValue(name);
                        fv.setSelected(true);
                    }
                    int index = field.getPossibleValues() == null ? 0 : field.getPossibleValues().size();
                    fv.setIndex(index);
                    if (field.getPossibleValues() == null) {
                        field.setPossibleValues(new ArrayList<FieldValue>());
                    }
                    if (!field.getPossibleValues().stream().anyMatch(n -> {
                        return n.getCode().equals(fv.getValue());
                    })) {
                        field.getPossibleValues().add(fv);
                    }
                }
                if (!codesLocation.isEmpty()) {
                    String[] codeValues = codesLocation.stream().toArray(String[]::new);
                    document.addStringMultiField(field.getFieldName(), codeValues);
                }
            }
        };
        processForEachFilteredByType(locationConsumer, FieldType.LOCATION);
    }

    protected void processDateElementType(InternalDocument document, Element element) throws ParseException {
        NodeList nodes = element.getElementsByTagName("activity-date");
        for (int j = 0; j < nodes.getLength(); ++j) {
            Element e = (Element) nodes.item(j);
            Field field = getField(e, FieldType.DATE);
            String localDate = e.getAttribute(ISO_ATTRIBUTE);
                String format = ISO_DATE;
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                if (localDate != null && !localDate.isEmpty()) {
                    document.addDateField(field.getUniqueFieldName(), sdf.parse(localDate));
                }
        }
    }


    protected void processTransactionElementType(InternalDocument document, Element iatiActivity,
                                                 Element activityProviderNode)  {
        NodeList nodes;
        nodes = iatiActivity.getElementsByTagName("transaction");
        for (int j = 0; j < nodes.getLength(); ++j) {
            String reference ;
            Element transactionElement = (Element) nodes.item(j);
            Field field = getField(transactionElement, TRANSACTION);

            if(field == null) {
                continue;
            }
            // Reference
            reference = transactionElement.getAttribute("ref");
            // Amount
            String localValue = transactionElement.getElementsByTagName("value").item(0).getChildNodes().item(0).getNodeValue();
            // Date
            String localDate = "";
            if(transactionElement.getElementsByTagName("transaction-date").item(0) != null) {
                NodeList transactionDate = transactionElement.getElementsByTagName("transaction-date").item(0).getChildNodes();
                if (transactionDate.getLength() == 0) {
                    localDate = ((Element) transactionDate).getAttribute(ISO_ATTRIBUTE);
                } else {
                    localDate = transactionDate.item(0).getNodeValue();

                }
                if (localDate != null && !isValidDate(localDate))
                {
                    localDate = transactionElement.getElementsByTagName("transaction-date").item(0).getAttributes().getNamedItem(ISO_ATTRIBUTE).getNodeValue();
                }
            }

            final String receivingOrganization = extractReceivingOrganization(transactionElement);

            Element providerNode = transactionElement.getElementsByTagName("provider-org").item(0) != null
                    ? (Element) transactionElement.getElementsByTagName("provider-org").item(0) : null;

            // if no provider tag, check if we have participating-org with role 1 (funding
            // if not use reporting org )
            if (providerNode == null) {
                if (activityProviderNode != null ) {
                    providerNode = activityProviderNode;
                } else {
                    //this should be moved to iati since its per activity
                    providerNode = iatiActivity.getElementsByTagName("reporting-org").item(0)
                            != null ? (Element) iatiActivity.getElementsByTagName("reporting-org")
                            .item(0) : null;
                }
            }
            final String providingOrganization = extractProvidingOrganization(providerNode);


            final String providerRef = (providerNode != null) ? providerNode.getAttribute("ref") : "";

            // Get the field for provider org
            Optional<Field> fieldValue = getFilterFieldList().stream().filter(n -> "provider-org".equals(n.getFieldName())).findFirst();
            // If it has filters set, check if this transaction
            // complies
            if (fieldValue.isPresent() && fieldValue.get().getFilters().size() > 0) {
                // See if the current transaction has the
                // correct provider organization
                Optional<String> optField = fieldValue.get().getFilters().stream().filter(n -> {
                    return n.equals(providingOrganization);
                }).findAny();
                if (!optField.isPresent()) {
                    // If it's not there, then move to the next
                    // transaction
                    continue;
                }
            }
            if (StringUtils.isBlank(providingOrganization)) {
                //if we don't have provider organization we should ingore the transaction
                continue;
            }

            Map<String, String> transactionFields = new HashMap<String, String>();
            transactionFields.put("date", localDate);
            transactionFields.put("providing-org", providingOrganization);
            transactionFields.put("receiving-org", receivingOrganization);
            transactionFields.put("provider-org-ref", providerRef);
            transactionFields.put("reference", reference);
            transactionFields.put("value", localValue);
            transactionFields.put("subtype", field.getSubType());
            document.addTransactionField("transaction" + field.getSubType() + "_" + j,
                    transactionFields);
        }

    }


    private void clearUsedValues() {
        for (Field field : getFields()) {
            if (field.getPossibleValues() != null) {
                for (FieldValue fv : field.getPossibleValues()) {
                    fv.setSelected(false);
                }
            }
        }
    }
    protected abstract String extractProvidingOrganization(Element providerNode);
    protected abstract String extractReceivingOrganization(Element e);

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
    protected abstract String getDateSubtype(Field field) ;

    /**
     * Search for name in the element, if not available return description. Otherwise return null
     * Each processor shall implemente where to look for name and description since it will depend on the iati version
     * @param fieldElement
     * @return
     */

    protected abstract String getNameFromElement(Element fieldElement);

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    protected Predicate<? super Field> getFieldsPredicate(Element e, FieldType fieldType) {
        switch (fieldType) {
            case TRANSACTION:
                String code = e.getElementsByTagName("transaction-type").item(0)
                        .getAttributes().getNamedItem("code").getNodeValue();
                return filteredField ->
                        (filteredField.getType().equals(fieldType)
                                && (filteredField.getSubType().equals(code)
                                || code.equals(filteredField.getSubTypeCode())));
            case DATE:
                  return filteredField ->
                       (filteredField.getType().equals(fieldType) &&
                               getDateSubtype(filteredField).equals(e.getAttribute("type")));
                default:
                return null;
        }
    }
    protected Field getField(Element element, FieldType fieldType){
        return getFields().stream()
                .filter(getFieldsPredicate(element, fieldType))
                .reduce((a, b) -> {
                    throw new IllegalStateException("Multiple elements: " + a + ", " + b);
                }).orElse(null);
    }

    protected boolean isValidDate(String dateString) {
        try {
            df.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

}
