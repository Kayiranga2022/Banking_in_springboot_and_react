package com.banking.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "Nah nigga this user already have an account!";

    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has benn Successfully Created!";

    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User With that Account NUmber doesn't Exist!";

    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_SUCCESS= "User With that Account NUmber Exist!";

    public static final String ACCOUNT_DEPOSITED_SUCCESS_CODE = "005";
    public static final String ACCOUNT_DEPOSITED_SUCCESS_MESSAGE= "Deposit have  Successfully Done!";

    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE= "Sorry, You are trying To withdraw much money than you have!";

    public static final String ACCOUNT_WITHDRAW_SUCCESS_CODE = "007";
    public static final String ACCOUNT_WITHDRAW_SUCCESS_MESSAGE= "Withdraw have  Successfully Done!";

    public static final String TRANSFER_SUCCESS_CODE = "008";
    public static final String TRANSFER_SUCCESS_MESSAGE= "Transfer have  Successfully Done!";

    public static final String ACCOUNT_UPDATE_SUCCESS_CODE="009";
    public static final String ACCOUNT_UPDATE_MESSAGE="account updated";
    public static String generateAccountNumber(){
        /**
         * 2024 + randomSevenDigits
         */
        Year curentYear = Year.now();
        int min = 1000000;
        int max = 9999999;

        //then ima generate those random numbers between min and max

        int randNumber = (int) Math.floor(Math.random() * (max- min + 1) +min);

        // convert the current and randNumber to strings, then concatenate them

        String year = String.valueOf(curentYear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();
    }
}
