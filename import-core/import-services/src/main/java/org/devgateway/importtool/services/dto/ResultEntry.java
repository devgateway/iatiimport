package org.devgateway.importtool.services.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultEntry {

    @JsonProperty("publisher_description") private String publisherDescription;
    @JsonProperty("publisher_iati_id") private String publisherIatiId;
    @JsonProperty("publisher_units") private String publisherUnits;
    @JsonProperty("publisher_implementation_schedule") private String publisherImplementationSchedule;
    @JsonProperty("description") private String description;
    @JsonProperty("publisher_ui") private String publisherUi;
    @JsonProperty("publisher_country") private String publisherCountry;
    @JsonProperty("publisher_record_exclusions") private String publisherRecordExclusions;
    @JsonProperty("title") private String title;
    @JsonProperty("type") private String type;
    @JsonProperty("publisher_constraints") private String publisherConstraints;
    @JsonProperty("publisher_frequency_select") private String publisherFrequencySelect;
    @JsonProperty("publisher_segmentation") private String publisherSegmentation;
    @JsonProperty("num_followers") private Integer numFollowers;
    @JsonProperty("publisher_organization_type") private Integer publisherOrganizationType;
    @JsonProperty("id") private String id;
    @JsonProperty("state") private String state;
    @JsonProperty("publisher_ui_url") private String publisherUiUrl;
    @JsonProperty("publisher_contact") private String publisherContact;
    @JsonProperty("publisher_field_exclusions") private String publisherFieldExclusions;
    @JsonProperty("image_url") private String imageUrl;
    @JsonProperty("publisher_timeliness") private String publisherTimeliness;
    @JsonProperty("publisher_source_type") private String publisherSourceType;
    @JsonProperty("is_organization") private Boolean isOrganization;
    @JsonProperty("publisher_contact_email") private String publisherContactEmail;
    @JsonProperty("publisher_refs") private String publisherRefs;
    @JsonProperty("package_count") private Integer packageCount;
    @JsonProperty("publisher_url") private String publisherUrl;
    @JsonProperty("license_id") private String licenseId;
    @JsonProperty("image_display_url") private String imageDisplayUrl;
    @JsonProperty("publisher_data_quality") private String publisherDataQuality;
    @JsonProperty("publisher_thresholds") private String publisherThresholds;
    @JsonProperty("name") private String name;
    @JsonProperty("publisher_first_publish_date") private String publisherFirstPublishDate;
    @JsonProperty("publisher_agencies") private String publisherAgencies;
    @JsonProperty("publisher_frequency") private String publisherFrequency;

    public String getPublisherDescription() {
        return publisherDescription;
    }

    public void setPublisherDescription(String publisherDescription) {
        this.publisherDescription = publisherDescription;
    }

    public String getPublisherIatiId() {
        return publisherIatiId;
    }

    public void setPublisherIatiId(String publisherIatiId) {
        this.publisherIatiId = publisherIatiId;
    }

    public String getPublisherUnits() {
        return publisherUnits;
    }

    public void setPublisherUnits(String publisherUnits) {
        this.publisherUnits = publisherUnits;
    }

    public String getPublisherImplementationSchedule() {
        return publisherImplementationSchedule;
    }

    public void setPublisherImplementationSchedule(String publisherImplementationSchedule) {
        this.publisherImplementationSchedule = publisherImplementationSchedule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisherUi() {
        return publisherUi;
    }

    public void setPublisherUi(String publisherUi) {
        this.publisherUi = publisherUi;
    }

    public String getPublisherCountry() {
        return publisherCountry;
    }

    public void setPublisherCountry(String publisherCountry) {
        this.publisherCountry = publisherCountry;
    }

    public String getPublisherRecordExclusions() {
        return publisherRecordExclusions;
    }

    public void setPublisherRecordExclusions(String publisherRecordExclusions) {
        this.publisherRecordExclusions = publisherRecordExclusions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPublisherConstraints() {
        return publisherConstraints;
    }

    public void setPublisherConstraints(String publisherConstraints) {
        this.publisherConstraints = publisherConstraints;
    }

    public String getPublisherFrequencySelect() {
        return publisherFrequencySelect;
    }

    public void setPublisherFrequencySelect(String publisherFrequencySelect) {
        this.publisherFrequencySelect = publisherFrequencySelect;
    }

    public String getPublisherSegmentation() {
        return publisherSegmentation;
    }

    public void setPublisherSegmentation(String publisherSegmentation) {
        this.publisherSegmentation = publisherSegmentation;
    }

    public Integer getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(Integer numFollowers) {
        this.numFollowers = numFollowers;
    }

    public Integer getPublisherOrganizationType() {
        return publisherOrganizationType;
    }

    public void setPublisherOrganizationType(Integer publisherOrganizationType) {
        this.publisherOrganizationType = publisherOrganizationType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPublisherUiUrl() {
        return publisherUiUrl;
    }

    public void setPublisherUiUrl(String publisherUiUrl) {
        this.publisherUiUrl = publisherUiUrl;
    }

    public String getPublisherContact() {
        return publisherContact;
    }

    public void setPublisherContact(String publisherContact) {
        this.publisherContact = publisherContact;
    }

    public String getPublisherFieldExclusions() {
        return publisherFieldExclusions;
    }

    public void setPublisherFieldExclusions(String publisherFieldExclusions) {
        this.publisherFieldExclusions = publisherFieldExclusions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublisherTimeliness() {
        return publisherTimeliness;
    }

    public void setPublisherTimeliness(String publisherTimeliness) {
        this.publisherTimeliness = publisherTimeliness;
    }

    public String getPublisherSourceType() {
        return publisherSourceType;
    }

    public void setPublisherSourceType(String publisherSourceType) {
        this.publisherSourceType = publisherSourceType;
    }

    public Boolean getOrganization() {
        return isOrganization;
    }

    public void setOrganization(Boolean organization) {
        isOrganization = organization;
    }

    public String getPublisherContactEmail() {
        return publisherContactEmail;
    }

    public void setPublisherContactEmail(String publisherContactEmail) {
        this.publisherContactEmail = publisherContactEmail;
    }

    public String getPublisherRefs() {
        return publisherRefs;
    }

    public void setPublisherRefs(String publisherRefs) {
        this.publisherRefs = publisherRefs;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public void setPublisherUrl(String publisherUrl) {
        this.publisherUrl = publisherUrl;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getImageDisplayUrl() {
        return imageDisplayUrl;
    }

    public void setImageDisplayUrl(String imageDisplayUrl) {
        this.imageDisplayUrl = imageDisplayUrl;
    }

    public String getPublisherDataQuality() {
        return publisherDataQuality;
    }

    public void setPublisherDataQuality(String publisherDataQuality) {
        this.publisherDataQuality = publisherDataQuality;
    }

    public String getPublisherThresholds() {
        return publisherThresholds;
    }

    public void setPublisherThresholds(String publisherThresholds) {
        this.publisherThresholds = publisherThresholds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisherFirstPublishDate() {
        return publisherFirstPublishDate;
    }

    public void setPublisherFirstPublishDate(String publisherFirstPublishDate) {
        this.publisherFirstPublishDate = publisherFirstPublishDate;
    }

    public String getPublisherAgencies() {
        return publisherAgencies;
    }

    public void setPublisherAgencies(String publisherAgencies) {
        this.publisherAgencies = publisherAgencies;
    }

    public String getPublisherFrequency() {
        return publisherFrequency;
    }

    public void setPublisherFrequency(String publisherFrequency) {
        this.publisherFrequency = publisherFrequency;
    }
}
