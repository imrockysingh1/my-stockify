package com.example.stockify.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailService {
    private final SesClient sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    public EmailService(SesClient sesClient){
        this.sesClient = sesClient;
    }

    public String sendEmail(String to , String subject , String body){

        Destination destination = Destination.builder()
                .toAddresses(to)
                .build();
        Content subjectContent = Content.builder().data(subject)
                .charset("UTF-8")
                .build();
        Content bodyContent = Content.builder()
                .data(body)
                .charset("UTF-8")
                .build();
        Body emailBody = Body.builder()
                .text(bodyContent)
                .build();
        Message message  = Message.builder()
                .subject(subjectContent)
                .body(emailBody)
                .build();
        SendEmailRequest request = SendEmailRequest.builder()
                .source(fromEmail)
                .destination(destination)
                .message(message)
                .build();
        SendEmailResponse response = sesClient.sendEmail(request);
        return response.messageId();

    }


}
