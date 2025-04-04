package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.TrainingService;
import org.apache.sling.models.annotations.Required;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(service= DusriServiceImpl.class, immediate = true)
public class DusriServiceImpl {

    @Reference
    private TrainingService trainingService;

    @Activate
    @Modified

    protected void bolo()
    {
        System.out.println(trainingService.getMembers());
    }
}
