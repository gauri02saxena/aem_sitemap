package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.services.IpService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class IpModel {

    @OSGiService
    private IpService ipService;

    @OSGiService(filter = "(component.name=com.adobe.aem.guides.wknd.core.services.impl.IpifyImpl)")
    private IpService ipifyService;

    @OSGiService(filter = "(component.name=com.adobe.aem.guides.wknd.core.services.impl.WhatismyipImpl)")
    private IpService whatIsMyIpService;

    public String getIpService()
    {
        return ipService.getImplementationName()+ ":" +ipService.getIp();
    }

    public String getIpifyIP() {
        return ipifyService.getImplementationName() + ": " + ipifyService.getIp();
    }

    public String getWhatIsMyIpIP() {
        return whatIsMyIpService.getImplementationName() + ": " + whatIsMyIpService.getIp();
    }
}
