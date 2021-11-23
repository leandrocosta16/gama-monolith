package com.thesis.gama.controller;

import com.thesis.gama.dto.UserSetDTO;
import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/register")
@Api(tags = "RegistrationController")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createUser(@Valid @RequestBody UserSetDTO userSetDto) throws AlreadyExistsException {
            return new ResponseEntity<>(userService.createAdmin(userSetDto), null, HttpStatus.CREATED);
    }


}
