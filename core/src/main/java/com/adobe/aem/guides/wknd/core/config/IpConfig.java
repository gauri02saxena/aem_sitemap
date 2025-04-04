package com.adobe.aem.guides.wknd.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="IP Service Configuration", description = "IP Service Configuration")
public @interface IpConfig {
    @AttributeDefinition(name= "API URL", description = "URL of API")
    String apiUrl();

//    @AttributeDefinition(name="String[] Label", description = "String[] Config",type= AttributeType.STRING)
//    String[] config_string_array() default {"item1", "item2"};
//
//    @AttributeDefinition(name="Boolean label", description = "Boolean Config",type= AttributeType.BOOLEAN)
//    boolean boolean_check() default true;
}
