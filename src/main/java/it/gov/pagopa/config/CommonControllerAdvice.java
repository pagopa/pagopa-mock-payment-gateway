package it.gov.pagopa.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonControllerAdvice {

    protected static final String APP_VERSION = "APP_VERSION";
    private final String appVersion;


    public CommonControllerAdvice() {
        super();
        String version = this.getClass().getPackage().getImplementationVersion();
        if (StringUtils.isBlank(version)) version = "0.0.0";
        this.appVersion = version;
    }

    @ModelAttribute
    public void advice(ModelMap modelMap) {
        modelMap.addAttribute(APP_VERSION, appVersion);
    }

}
