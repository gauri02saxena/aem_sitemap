package com.adobe.aem.guides.wknd.core.services.impl;


import com.adobe.aem.guides.wknd.core.config.IpConfig;
import com.adobe.aem.guides.wknd.core.services.IpService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = IpService.class, immediate = true)
@Designate(ocd= IpConfig.class)
public class IpifyImpl implements IpService {

    private  String apiUrl;

    @Activate
    @Modified
    protected void activate(IpConfig config)
    {
        this.apiUrl= config.apiUrl();
    }


    @Override
    public String getIp() {
        return fetchIP(apiUrl);
    }

    @Override
    public String getImplementationName() {
        return "IpifyImpl";
    }

    private String fetchIP(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while((line=reader.readLine())!=null)
            {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());
            return json.getString("ip");
        } catch (Exception e) {
            return "Error fetching IP: " + e.getMessage();
        }
    }
}

