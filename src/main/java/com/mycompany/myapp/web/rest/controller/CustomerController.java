package com.mycompany.myapp.web.rest.controller;

import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.service.CustomerService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        Customer customer = customerService.findOne(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/auth")
    public ResponseEntity<String> authenticate(@RequestBody UserCredentials credentials) {
        Optional<String> result = customerService.authenticate(credentials.getUsername(), credentials.getPassword());
        return result
            .map(
                res ->
                    res.equals("Authentication successful")
                        ? ResponseEntity.ok(res)
                        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res)
            )
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unexpected error"));
    }

    static class UserCredentials {

        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
