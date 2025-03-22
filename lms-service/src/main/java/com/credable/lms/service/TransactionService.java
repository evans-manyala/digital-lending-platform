// lms-service/src/main/java/com/credable/lms/service/TransactionService.java

package com.credable.lms.service;

import com.credable.common.exception.ServiceException;
import com.credable.lms.soap.transaction.ObjectFactory;
import com.credable.lms.soap.transaction.TransactionData;
import com.credable.lms.soap.transaction.TransactionsRequest;
import com.credable.lms.soap.transaction.TransactionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

@Service
public class TransactionService {

    @Value("${soap.transaction.url}")
    private String transactionUrl;

    @Value("${soap.username}")
    private String username;

    @Value("${soap.password}")
    private String password;

    private final WebServiceTemplate transactionWebServiceTemplate;

    @Autowired
    public TransactionService(WebServiceTemplate transactionWebServiceTemplate) {
        this.transactionWebServiceTemplate = transactionWebServiceTemplate;
    }

    public List<TransactionData> getTransactionData(String customerNumber) {
        try {
            ObjectFactory factory = new ObjectFactory();
            TransactionsRequest request = factory.createTransactionsRequest();
            request.setCustomerNumber(customerNumber);
            
            TransactionsResponse response = (TransactionsResponse) transactionWebServiceTemplate.marshalSendAndReceive(
                    transactionUrl,
                    request,
                    new SoapAuthCallback(username, password)
            );
            
            if (response == null || response.getTransactions() == null) {
                throw new ServiceException("Failed to retrieve transaction data");
            }
            
            return response.getTransactions();
            
        } catch (Exception e) {
            throw new ServiceException("Error retrieving transaction data: " + e.getMessage(), e);
        }
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