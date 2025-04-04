package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CourseDetails {

    @ValueMapValue
    private String coursename;

    @ValueMapValue
    private Integer score;

    public String getCourseName() {
        return coursename;
    }

    public Integer getScore() {
        return score;
    }

    public String getGrade() {
        if(getScore()<33)
        {
            return "F";
        }
        else if(getScore()>=33 && getScore()<75)
        {
            return "P";
        }
        else{
            return "A";
        }

    }
}
