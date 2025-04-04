package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.services.TrainingService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import java.util.List;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ServiceModel {

    @OSGiService
    private TrainingService trainingService;

    public List<String> getServiceMembers()
    {
        return trainingService.getMembers();
    }
}
