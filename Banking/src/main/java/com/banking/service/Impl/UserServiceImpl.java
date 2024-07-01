package com.banking.service.Impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


import com.banking.dto.*;

import org.springframework.beans.factory.annotation.Autowired;

//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banking.entity.User;
import com.banking.repository.UserRepository;
import com.banking.utils.AccountUtils;

@Service

public class UserServiceImpl implements UserService {

        @Autowired
        UserRepository userRepository;

        @Autowired
        EmailService emailService;

        @Autowired
        TransactionService transactionService;

       // @Autowired
       // PasswordEncoder passwordEncoder;

      //  @Autowired
       // AuthenticationManager authenticationManager;

       // @Autowired
       // JwtTokenProvider jwtTokenProvider;
        @Override
        public BankResponse createAccount(UserRequest userRequest) {
                /**
                 * this creation means saving new user into the db
                 * checking if the user already has an account
                 */

                if (userRepository.existsByEmail(userRequest.getEmail())) {
                        return BankResponse.builder()

                                        .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                                        .accountInfo(null)
                                        .build();

                }
                User newUser = User.builder()
                                .firstName(userRequest.getFirstName())
                                .lastName(userRequest.getLastName())
                                .otherName(userRequest.getOtherName())
                                .gender(userRequest.getGender())
                                .address(userRequest.getAddress())
                                .stateOfOrigin(userRequest.getStateOfOrigin())
                                .accountNumber(AccountUtils.generateAccountNumber())
                                .accountBalance(BigDecimal.ZERO)
                                .email(userRequest.getEmail())
                               // .password(passwordEncoder.encode(userRequest.getPassword()))
                        .password(userRequest.getPassword())
                                .phoneNumber(userRequest.getPhoneNumber())
                                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                                .status("ACTIVE")
                        .role(userRequest.getRole())
                                .build();

                User savedUser = userRepository.save(newUser);

                // Send Email Alerts

                EmailDetails emailDetails = EmailDetails.builder()
                                .recipient(savedUser.getEmail())
                                .subject("ACCOUNT CREATION")
                                .messageBody("Congz Your Bank Account has been Successfully created.\nYour Account Details:\n"
                                                +
                                                "Account Name:" + savedUser.getFirstName() + " "
                                                + savedUser.getLastName() + " " + savedUser.getOtherName()
                                                + "\nAccount Number: " + savedUser.getAccountNumber())

                                .build();
                emailService.sendEmailAlert(emailDetails);

                return BankResponse.builder()

                                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                                .accountInfo(AccountInfo.builder()
                                                .accountBalance(savedUser.getAccountBalance())
                                                .accountNumber(savedUser.getAccountNumber())
                                                .accountName(savedUser.getFirstName() + " " + savedUser.getLastName()
                                                                + " " + savedUser.getOtherName())
                                                .build())
                                .build();
        }

      /*  public BankResponse login(LoginDto loginDto){
                Authentication  authentication = null;
                authentication= authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
                );
                 EmailDetails loginAlert = EmailDetails.builder()
                .subject("you are login!")
                         .recipient(loginDto.getEmail())
                         .messageBody("you logged in and you're welcome but if you didn't initiate this action immediately contact your bank!")

                         .build();
                 emailService.sendEmailAlert(loginAlert);
                 return BankResponse.builder()
                         .responseCode("login success")
                         .responseMessage(jwtTokenProvider.generateToken(authentication))
                         .build();
        }

       */

        @Override
        public BankResponse balanceEnquiry(ActionsRequest request) {
                boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
                if (!isAccountExist) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
                return BankResponse.builder()
                                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                                .accountInfo(AccountInfo.builder()
                                                .accountBalance(foundUser.getAccountBalance())
                                                .accountNumber(request.getAccountNumber())
                                                .accountName(foundUser.getFirstName() + " " + foundUser.getLastName()
                                                                + " " + foundUser.getOtherName())

                                                .build())

                                .build();

        }

        @Override
        public String nameEnquiry(ActionsRequest request) {
                boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
                if (!isAccountExist) {
                        return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;

                }
                User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
                return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
        }

        @Override
        public BankResponse depositAccount(DepositWithdrawRequest request) {
                // checking if the account exits

                boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
                if (!isAccountExist) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }
                User userToDeposit = userRepository.findByAccountNumber(request.getAccountNumber());
                userToDeposit.setAccountBalance(userToDeposit.getAccountBalance().add(request.getAmount()));

                userRepository.save(userToDeposit);

                // save Transaction
                TransactionDto transactionDto = TransactionDto.builder()
                                .accountNumber(userToDeposit.getAccountNumber())
                                .transactionType("Deposit")
                                .amount(request.getAmount())
                                .build();
                transactionService.saveTransaction(transactionDto);

                return BankResponse.builder()
                                .responseCode(AccountUtils.ACCOUNT_DEPOSITED_SUCCESS_CODE)
                                .responseMessage(AccountUtils.ACCOUNT_DEPOSITED_SUCCESS_MESSAGE)
                                .accountInfo(AccountInfo.builder()
                                                .accountName(userToDeposit.getFirstName() + " "
                                                                + userToDeposit.getLastName() + " "
                                                                + userToDeposit.getOtherName())

                                                .accountBalance(userToDeposit.getAccountBalance())
                                                .accountNumber(request.getAccountNumber())
                                                .build())

                                .build();
        }

        @Override
        public BankResponse withdrawAccount(DepositWithdrawRequest request) {
                // checking if account exits
                boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
                if (!isAccountExist) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }
                // check if amount you want to withdraw is not more than what you have
                User userToWithdraw = userRepository.findByAccountNumber(request.getAccountNumber());
                BigInteger availableBalance = userToWithdraw.getAccountBalance().toBigInteger();
                BigInteger withdrawAmount = request.getAmount().toBigInteger();
                if (availableBalance.intValue() < withdrawAmount.intValue()) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                                        .accountInfo(null)

                                        .build();
                }

                else {
                        userToWithdraw.setAccountBalance(
                                        userToWithdraw.getAccountBalance().subtract(request.getAmount()));
                        userRepository.save(userToWithdraw);

                        // save Transaction
                        TransactionDto transactionDto = TransactionDto.builder()
                                        .accountNumber(userToWithdraw.getAccountNumber())
                                        .transactionType("Withdraw")
                                        .amount(request.getAmount())
                                        .build();

                        transactionService.saveTransaction(transactionDto);
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_WITHDRAW_SUCCESS_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_WITHDRAW_SUCCESS_MESSAGE)
                                        .accountInfo(AccountInfo.builder()
                                                        .accountNumber(request.getAccountNumber())
                                                        .accountName(userToWithdraw.getFirstName() + " "
                                                                        + userToWithdraw.getLastName() + " "
                                                                        + userToWithdraw.getOtherName())
                                                        .accountBalance(userToWithdraw.getAccountBalance())

                                                        .build())
                                        .build();
                }

        }

        @Override
        public BankResponse Transfer(TransferRequest request) {
                // get the account to debit
                // check if the amount i'm debiting is not more than the current balance
                // debit the account
                // get the account to credit
                // credit the account

                boolean isDestinationAccountExist = userRepository
                                .existsByAccountNumber(request.getDestinationAccountNumber());
                if (!isDestinationAccountExist) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
                if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                                        .accountInfo(null)

                                        .build();
                }
                sourceAccountUser
                                .setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
                String senderUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName();
                userRepository.save(sourceAccountUser);
                EmailDetails debitAlert = EmailDetails.builder()
                                .subject("Debit Alert")
                                .recipient(sourceAccountUser.getEmail())
                                .messageBody("The sum of " + request.getAmount()
                                                + "has been Withdrawn from your Account!  Your current Balance is "
                                                + sourceAccountUser.getAccountBalance())
                                .build();

                emailService.sendEmailAlert(debitAlert);

                User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
                destinationAccountUser
                                .setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
                // String receiverUsername = destinationAccountUser.getFirstName() + " "
                // +destinationAccountUser.getLastName();
                userRepository.save(destinationAccountUser);
                EmailDetails creditAlert = EmailDetails.builder()
                                .subject("Credit Alert")
                                .recipient(destinationAccountUser.getEmail())
                                .messageBody("The sum of " + request.getAmount() + "has been Sent on your Account from "
                                                + senderUsername + " Your current Balance is "
                                                + destinationAccountUser.getAccountBalance())
                                .build();

                emailService.sendEmailAlert(creditAlert);
                // save Transaction
                TransactionDto transactionDto = TransactionDto.builder()
                                .accountNumber(destinationAccountUser.getAccountNumber())
                                .transactionType("Deposit")
                                .amount(request.getAmount())
                                .build();
                transactionService.saveTransaction(transactionDto);

                return BankResponse.builder()
                                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                                .accountInfo(null)

                                .build();
        }

        public User SaveUser(User user) {

                return userRepository.save(user);
        }

        // public static void main(String[] args){
        // UserServiceImpl userService = new UserServiceImpl();
        // System.out.println(userService.passwordEncoder.encode( "1234" ));
        // }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public BankResponse updateUser(Long id, UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setOtherName(userRequest.getOtherName());
            user.setGender(userRequest.getGender());
            user.setAddress(userRequest.getAddress());
            user.setStateOfOrigin(userRequest.getStateOfOrigin());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setAlternativePhoneNumber(userRequest.getAlternativePhoneNumber());
            user.setRole(userRequest.getRole());
            userRepository.save(user);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(user.getEmail())
                    .subject("ACCOUNT UPDATE")
                    .messageBody("Your Bank Account has been Successfully updated.\nYour Account Details:\n" +
                            "Account Name:" + user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName() +
                            "\nAccount Number: " + user.getAccountNumber())
                    .build();
            emailService.sendEmailAlert(emailDetails);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_UPDATE_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_UPDATE_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(user.getAccountBalance())
                            .accountNumber(user.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                            .build())
                    .build();
        } else {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public User findById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

    @Override
    public BankResponse login(LoginDto loginDto) {
        // Assuming the login logic and authentication check
        // Replace with actual login logic
        boolean isAuthenticated = authenticateUser(loginDto); // You need to implement this method

        if (isAuthenticated) {
            // Generate token or any other response on successful login
            return BankResponse.builder()
                    .responseCode("200")
                    .responseMessage("Login successful")
                    .accountInfo(null) // Add appropriate account info if needed
                    .build();
        } else {
            return BankResponse.builder()
                    .responseCode("401")
                    .responseMessage("Unauthorized")
                    .accountInfo(null)
                    .build();
        }
    }

    private boolean authenticateUser(LoginDto loginDto) {
        // Implement the actual authentication logic here
        // For example, check the user credentials against the database
        return userRepository.existsByEmailAndPasswordAndRole(loginDto.getEmail(), loginDto.getPassword(), loginDto.getRole());
    }
    @Override
    public BankResponse deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(id);
            return BankResponse.builder()
                    .responseCode("200")
                    .responseMessage("User deleted successfully")
                    .build();
        } else {
            return BankResponse.builder()
                    .responseCode("404")
                    .responseMessage("User not found")
                    .build();
        }
    }




}

