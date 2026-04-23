package com.ironhack.smarttourism.entity;

import lombok.Data;
import java.util.List;

@Data
public class EmailOptions {
    private String fromEmail;   // Göndərən mail (Mailtrap-da təsdiqlənmiş domain)
    private String fromName;    // Göndərən adı
    private String toEmail;     // Alıcı mail
    private String subject;     // Mövzu
    private String html;        // HTML formatında mesaj (Bizim link bura gedir)
    private String text;        // Sadə mətn formatında mesaj

    // SDK-nın versiyasına görə əlavə olaraq attachments və ya category ola bilər
}