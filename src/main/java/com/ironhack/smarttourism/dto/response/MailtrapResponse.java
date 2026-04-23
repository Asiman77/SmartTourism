package com.ironhack.smarttourism.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class MailtrapResponse {
    private boolean success;          // İşlem uğurludurmu?
    private List<String> messageIds;  // Göndərilən mesajların ID siyahısı
    private List<String> errors;       // Əgər xəta varsa, xəta mesajları

    public boolean isSuccess() {
        return success;
    }
}
