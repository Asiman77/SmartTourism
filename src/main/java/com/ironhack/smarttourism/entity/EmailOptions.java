package com.ironhack.smarttourism.entity;

import lombok.Data;

@Data
public class EmailOptions {
    private String fromEmail;   // Sender mail (A domain that has been verified in "Mailtrap")
    private String fromName;    // Sender adı
    private String toEmail;     // Receiver mail
    private String subject;     // subject
    private String html;        // message in HTML format  (Our link goes there)
    private String text;        // message in text format

    // Depending on version of SDK , there might be extra attachments or category
}