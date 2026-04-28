package com.ironhack.smarttourism.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class MailtrapResponse {
    private boolean success;          // is process successfull?
    private List<String> messageIds;  // id list of the sent messages
    private List<String> errors;       // If there is an error,messages of it

    public boolean isSuccess() {
        return success;
    }
}
