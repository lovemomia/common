package cn.momia.common.webapp.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerConfigurer.class);

    private XMLConfiguration xmlConf;

    public void setXmlConf(XMLConfiguration xmlConf) {
        this.xmlConf = xmlConf;
    }

    public void init() {
        reload();
    }

    public void reload() {
        String loggerLevel = xmlConf.getString("Logger.Level");
        if (StringUtils.isBlank(loggerLevel)) {
            LOGGER.warn("Logger.Level config is empty");
            return;
        }

        LOGGER.info("set root logger level to: {}", loggerLevel);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.toLevel(loggerLevel, Level.INFO));
    }
}
