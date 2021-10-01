package it.gov.pagopa.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonControllerAdvice {

    private static final String APP_VERSION = "APP_VERSION";
    private static final String AZURE_API_STATIC_RES = "AZURE_API_STATIC_RES";
    private final String appVersion;

    @Value("${server.azure-api-static-res}")
    private String azureApiStaticRes;

    public CommonControllerAdvice() {
        super();
        String version = this.getClass().getPackage().getImplementationVersion();
        if (StringUtils.isBlank(version)) version = "0.0.0";
        this.appVersion = version;
    }

    @ModelAttribute
    public void advice(ModelMap modelMap) {
        modelMap.addAttribute(APP_VERSION, appVersion);
        modelMap.addAttribute(AZURE_API_STATIC_RES, StringUtils.removeEnd(azureApiStaticRes, "/"));
    }

}
