package cn.momia.common.webapp.config;

import org.apache.commons.configuration.XMLConfiguration;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

public class XmlPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private XMLConfiguration xmlConf;

    public void setXmlConf(XMLConfiguration xmlConf) {
        this.xmlConf = xmlConf;
    }

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
        String propVal = null;

        if (systemPropertiesMode == PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE) propVal = resolveSystemProperty(placeholder);
        if (propVal == null) propVal = xmlConf.getString(placeholder);
        if (propVal == null) propVal = resolvePlaceholder(placeholder, props);
        if (propVal == null && systemPropertiesMode == PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK) propVal = resolveSystemProperty(placeholder);

        return propVal;
    }
}
