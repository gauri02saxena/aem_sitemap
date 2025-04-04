package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NishantModel {

    @ValueMapValue
    private String name;

    @ValueMapValue
    private int age;

    @ValueMapValue(name = "class")
    private int className;

    @ValueMapValue
    private String image;

    public String getImage() {
        return image;
    }

    @ChildResource(name = "field")
    private List<CourseDetails> courseDetails;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getClassName() {
        return className;
    }

    public List<CourseDetails> getCourseDetails() {
        return courseDetails;
    }


}
