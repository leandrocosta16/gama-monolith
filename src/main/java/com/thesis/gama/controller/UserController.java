package com.thesis.gama.controller;

import com.thesis.gama.dto.UserGetDTO;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@Api(tags = "UserController")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public UserGetDTO getMyUser(@RequestHeader("Authorization") String authorizationToken) throws NoDataFoundException {
        return this.userService.getMyUserDetails(authorizationToken);
    }


}