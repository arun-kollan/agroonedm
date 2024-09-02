package com.greenario.common.rest;

import com.greenario.AgroOneConfiguration;
import com.greenario.common.api.GreenarioEngine;
import com.greenario.cropcondition.api.CropConditionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

/** Configuration for REST service. */
@Configuration
@ComponentScan("com.greenario")
@EnableTransactionManagement
public class RestConfiguration {

    private final String schemaName;

    public RestConfiguration(@Value("${greenario.schemaName:GREENARIO}") String schemaName) {
        this.schemaName = schemaName;
    }

    @Bean
    public CropConditionService getCropConditionService(GreenarioEngine greenarioEngine) {
        return greenarioEngine.getCropConditionService();
    }

    @Bean
    @ConditionalOnMissingBean(GreenarioEngine.class)
    public GreenarioEngine getGreenarioEngine(AgroOneConfiguration agroOneConfiguration) throws SQLException {
        return SpringGreenarioEngine.buildGreenarioEngine(agroOneConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean(AgroOneConfiguration.class)
    public AgroOneConfiguration agroOneConfiguration(
            DataSource dataSource,
            @Qualifier("greenarioPropertiesFileName") String propertiesFileName,
            @Qualifier("greenarioPropertiesDelimiter") String delimiter) {
        return new AgroOneConfiguration.Builder(dataSource, true, schemaName)
                .initGreenarioProperties(propertiesFileName, delimiter)
                .build();
    }

    @Bean
    public String greenarioPropertiesFileName() {
        return "/greenario.properties";
    }

    @Bean
    public String greenarioPropertiesDelimiter() {
        return "|";
    }

    // Needed for injection into Jackson deserializer.
    @Bean
    public SpringHandlerInstantiator handlerInstantiator(ApplicationContext context) {
        return new SpringHandlerInstantiator(context.getAutowireCapableBeanFactory());
    }
}
