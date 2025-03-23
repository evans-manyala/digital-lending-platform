// lms-service/src/main/java/com/credable/lms/config/SoapConfig.java
package com.credable.lms.config;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

@Configuration
public class SoapConfig {

    @Value("${cbs.soap.username}")
    private String username;

    @Value("${cbs.soap.password}")
    private String password;

    @Bean
    public Jaxb2Marshaller kycMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.credable.lms.soap.kyc");
        return marshaller;
    }

    @Bean
    public Jaxb2Marshaller transactionMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.credable.lms.soap.transaction");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate kycWebServiceTemplate() {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(kycMarshaller());
        webServiceTemplate.setUnmarshaller(kycMarshaller());
        webServiceTemplate.setMessageSender(httpComponentsMessageSender());
        return webServiceTemplate;
    }

    @Bean
    public WebServiceTemplate transactionWebServiceTemplate() {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(transactionMarshaller());
        webServiceTemplate.setUnmarshaller(transactionMarshaller());
        webServiceTemplate.setMessageSender(httpComponentsMessageSender());
        return webServiceTemplate;
    }

    @Bean
    public HttpComponentsMessageSender httpComponentsMessageSender() {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        httpComponentsMessageSender.setCredentials(credentials);
        return httpComponentsMessageSender;
    }
}