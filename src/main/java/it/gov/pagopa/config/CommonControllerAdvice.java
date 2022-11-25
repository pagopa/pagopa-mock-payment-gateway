package it.gov.pagopa.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonControllerAdvice {

    private static final String APP_VERSION = "APP_VERSION";
    private static final String AZURE_API_PUBLIC_URL = "AZURE_API_PUBLIC_URL";
    protected static final String MOCK_PROFILE = "MOCK_PROFILE";
    private final String appVersion;

    @Value("${server.public-url}")
    private String publicUrl;

    @Value("${mock-profile}")
    private String mockProfile;

    public CommonControllerAdvice() {
        super();
        String version = this.getClass().getPackage().getImplementationVersion();
        this.appVersion = StringUtils.isBlank(version) ? "0.0.0" : version;
    }

    @ModelAttribute
    public void advice(ModelMap modelMap) {
        modelMap.addAttribute(APP_VERSION, appVersion);
        modelMap.addAttribute(AZURE_API_PUBLIC_URL, StringUtils.removeEnd(publicUrl, "/"));
        modelMap.addAttribute(MOCK_PROFILE, mockProfile);
    }

}
