package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.TrainingService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

@Component(service = TrainingService.class)
public class TrainingServiceImpl implements TrainingService{
    private final List<String> members= new ArrayList<>();

    @Activate
    protected void activate()
    {
        members.add("gauri");
        members.add("aditya");
        System.out.println(members);
    }

    @Modified
    protected void modified()
    {
        members.add("fanni");
        System.out.println(members);
    }

    @Deactivate
    public void deactivate()
    {
        members.clear();
        System.out.println(members);
    }

    @Override
    public List<String> getMembers()
    {
        return members;
    }

}
