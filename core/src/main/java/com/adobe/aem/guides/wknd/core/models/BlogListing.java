package com.adobe.aem.guides.wknd.core.models;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BlogListing {
    @SlingObject
    private ResourceResolver resourceResolver;
    @ChildResource(name = "field")
    private List<Resource> blogpath;

    public List<BlogDetails> getBlogs () {
        if (blogpath == null ) {
            return Collections.emptyList();
        }
        List<BlogDetails> blogsList = new ArrayList<>();
        for (Resource item : blogpath) {
            String path = item.getValueMap().get("blogPages", String.class);
            if (path != null) {
                Resource blogDetailResource = resourceResolver.getResource(path + "/jcr:content");

                if (blogDetailResource != null) {
                    BlogDetails blog = blogDetailResource.adaptTo(BlogDetails.class);
                    if (blog != null) {
                        blogsList.add(blog);
                    }
                }
            }
        }

        return blogsList;
    }
}