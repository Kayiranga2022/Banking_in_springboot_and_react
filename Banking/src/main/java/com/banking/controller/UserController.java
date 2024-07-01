package com.banking.controller;

import com.banking.dto.*;
import com.banking.entity.User;
import com.banking.service.Impl.UserService;
import com.banking.service.Impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/user")
@Tag(name = "User account management APIs")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(
            summary = "Create New User Account",
            description = "Creating New User and assigning an account ID"
    )

    @ApiResponse(
            responseCode = "200",
            description = "status 200 User Created"
    )
    // @Autowired
    //private UserServiceImpl userServicei;

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {

        return userService.createAccount(userRequest);

    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(
            summary = "Get All Users",
            description = "Retrieve all users"
    )
    @ApiResponse(
            responseCode = "200",
            description = "status 200 Success"
    )
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Update User Account",
            description = "Update an existing user's details"
    )
    @ApiResponse(
            responseCode = "200",
            description = "status 200 User Updated"
    )
    @PutMapping("/{id}")
    public BankResponse updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        return userService.updateUser(id, userRequest);
    }

    @Operation(
            summary = "Delete User",
            description = "Delete a user by ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "status 200 User Deleted"
    )
    @DeleteMapping("/{id}")
    public BankResponse deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }


//@PostMapping("/login")
//public BankResponse login(@RequestBody LoginDto loginDto){
    //  return userService.login(loginDto);
//}

//@PostMapping("/save")
//ResponseEntity<User>RegisterUser(@RequestBody User user){
    // User adduser=userServicei.SaveUser(user);
    // return ResponseEntity.ok().body(user);
//}

    @Operation(
            summary = "Balance Enquiry",
            description = "Checking The user Balance"
    )

    @ApiResponse(
            responseCode = "200",
            description = "status 200 Success"
    )
    @PostMapping(value = "/BalanceEnquiry", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BankResponse balanceEnquiry(@RequestBody ActionsRequest request) {
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/NameEnquiry")
    public String nameEnquiry(@RequestBody ActionsRequest request) {
        return userService.nameEnquiry(request);
    }

    @PostMapping("/Deposit")
    public BankResponse depositAccount(@RequestBody DepositWithdrawRequest request) {
        return userService.depositAccount(request);
    }

    @PostMapping("/Withdraw")
    public BankResponse withdrawAccount(@RequestBody DepositWithdrawRequest request) {
        return userService.withdrawAccount(request);
    }

    @PostMapping("/Transfer")
    public BankResponse transferAmount(@RequestBody TransferRequest request) {
        return userService.Transfer(request);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        BankResponse response = userService.login(loginDto);
        if (response == null) {
            // Handle null response case if it occurs (for safety)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
        if ("200".equals(response.getResponseCode())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}
