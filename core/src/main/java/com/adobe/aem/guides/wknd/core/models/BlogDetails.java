package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BlogDetails {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String author;

    @ValueMapValue(name = "image/fileReference")
    private String thumbnailFileReference;

    @Self
    private Resource resource;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getThumbnail() {
        if (thumbnailFileReference != null && !thumbnailFileReference.isEmpty()) {
            return thumbnailFileReference;
        }

        Resource imageResource = resource.getChild("image/file");
        if (imageResource != null) {
            return resource.getPath() + "/image/file";
        }

        return null;
    }
}
