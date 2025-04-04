package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MyComponentModel {

    // Injecting properties directly from the resource (e.g., component properties in AEM)
    @Inject
    String mainTitle; // Inject the title from the page or resource

    @Inject
    String mainDescription; // Inject the description

    @Inject
    Boolean isEnabled; // Inject a boolean value (is the component enabled?)

    @Inject
    int pageSize; // Inject an integer value (e.g., number of blogs)

    @Inject
    String type; // Inject a string value (type of content or component)

    // Use @PostConstruct if you need additional initialization after injections
    @PostConstruct
    protected void init() {
        // Any additional initialization logic, if needed
        // For example, log the values or set defaults
    }

    // Getters for each field to allow access in HTL
    public String getMainTitle() {
        return mainTitle;
    }

    public String getMainDescription() {
        return mainDescription;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getType() {
        return type;
    }
}
