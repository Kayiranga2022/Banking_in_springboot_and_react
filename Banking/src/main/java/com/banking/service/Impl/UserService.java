package com.banking.service.Impl;


import com.banking.dto.*;
import com.banking.entity.User;

import java.util.List;

public interface  UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(ActionsRequest request);
    String nameEnquiry(ActionsRequest request);

    BankResponse depositAccount (DepositWithdrawRequest request);

    BankResponse withdrawAccount (DepositWithdrawRequest request);

    BankResponse Transfer(TransferRequest request);

    List<User> getAllUsers();
    BankResponse updateUser(Long id, UserRequest userRequest);

    User findById(Long id);

    BankResponse login(LoginDto loginDto);
    BankResponse deleteUser(Long id);


}
