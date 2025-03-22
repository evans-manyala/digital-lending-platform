// lms-service/src/main/java/com/credable/lms/service/KycService.java

package com.credable.lms.service;

import com.credable.common.exception.ServiceException;
import com.credable.lms.model.CustomerInfo;
import com.credable.lms.repository.CustomerInfoRepository;
import com.credable.lms.soap.kyc.Customer;
import com.credable.lms.soap.kyc.CustomerRequest;
import com.credable.lms.soap.kyc.CustomerResponse;
import com.credable.lms.soap.kyc.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class KycService {

    @Value("${soap.kyc.url}")
    private String kycUrl;

    @Value("${soap.username}")
    private String username;

    @Value("${soap.password}")
    private String password;

    private final WebServiceTemplate kycWebServiceTemplate;
    private final CustomerInfoRepository customerInfoRepository;

    @Autowired
    public KycService(WebServiceTemplate kycWebServiceTemplate, CustomerInfoRepository customerInfoRepository) {
        this.kycWebServiceTemplate = kycWebServiceTemplate;
        this.customerInfoRepository = customerInfoRepository;
    }

    public CustomerInfo getCustomerInfo(String customerNumber) {
        Optional<CustomerInfo> existingCustomer = customerInfoRepository.findByCustomerNumber(customerNumber);
        
        if (existingCustomer.isPresent()) {
            return existingCustomer.get();
        }
        
        try {
            ObjectFactory factory = new ObjectFactory();
            CustomerRequest request = factory.createCustomerRequest();
            request.setCustomerNumber(customerNumber);
            
            CustomerResponse response = (CustomerResponse) kycWebServiceTemplate.marshalSendAndReceive(
                    kycUrl,
                    request,
                    new SoapAuthCallback(username, password)
            );
            
            if (response == null || response.getCustomer() == null) {
                throw new ServiceException("Failed to retrieve customer information");
            }
            
            Customer customer = response.getCustomer();
            CustomerInfo customerInfo = mapToCustomerInfo(customer);
            return customerInfoRepository.save(customerInfo);
            
        } catch (Exception e) {
            throw new ServiceException("Error retrieving customer information: " + e.getMessage(), e);
        }
    }
    
    private CustomerInfo mapToCustomerInfo(Customer customer) {
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerNumber(customer.getCustomerNumber());
        customerInfo.setFirstName(customer.getFirstName());
        customerInfo.setLastName(customer.getLastName());
        customerInfo.setIdNumber(customer.getIdNumber());
        customerInfo.setIdType(customer.getIdType().value());
        customerInfo.setPhoneNumber(customer.getPhoneNumber());
        customerInfo.setEmailAddress(customer.getEmailAddress());
        customerInfo.setStatus(customer.getStatus().value());
        customerInfo.setGender(customer.getGender().value());
        customerInfo.setDateOfBirth(customer.getDateOfBirth().toGregorianCalendar().toZonedDateTime().toLocalDate());
        customerInfo.setCreatedAt(LocalDateTime.now());
        customerInfo.setUpdatedAt(LocalDateTime.now());
        return customerInfo;
    }
    
    private static class SoapAuthCallback implements WebServiceMessageCallback {
        private final String username;
        private final String password;

        public SoapAuthCallback(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
            SoapMessage soapMessage = (SoapMessage) message;
            soapMessage.getSoapHeader().addHeaderElement(
                    soapMessage.getSoapHeader().getNamespaceURI().createQName("username", "auth"))
                    .setText(username);
            soapMessage.getSoapHeader().addHeaderElement(
                    soapMessage.getSoapHeader().getNamespaceURI().createQName("password", "auth"))
                    .setText(password);
        }
    }
}