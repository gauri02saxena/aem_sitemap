package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NestedMulti3Bean {


    @Inject
    private String title;

    @Inject
    private String link;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
