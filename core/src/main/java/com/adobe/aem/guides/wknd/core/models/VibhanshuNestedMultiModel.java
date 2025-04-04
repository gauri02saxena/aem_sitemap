package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import javax.inject.Inject;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class VibhanshuNestedMultiModel {

//    @Inject
//    private String title;
//
//    @Inject
//    private String link;

    @ChildResource(name = "field")
    private List<NestedMulti2> nestedMulti2List;

//    public String getTitle() {
//        return title;
//    }
//
//    public String getLink() {
//        return link;
//    }

    public List<NestedMulti2> getNestedMulti2List() {
        return nestedMulti2List;
    }
}
