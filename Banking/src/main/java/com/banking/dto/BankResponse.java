package com.banking.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankResponse {
    private String responseCode;
    private  String responseMessage;
    private  AccountInfo accountInfo;

    public boolean getStatus() {

        return true;
    }
}
