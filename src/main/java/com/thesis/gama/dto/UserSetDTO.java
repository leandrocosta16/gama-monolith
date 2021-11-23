package com.thesis.gama.dto;

import com.thesis.gama.model.Account;
import com.thesis.gama.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSetDTO {
    @Email(message = "a valid email must be entered")
    @NotNull(message = "email must be entered")
    private String email;
    @NotNull
    private String password;
    @NotNull(message = "first name must be entered")
    private String firstName;
    @NotNull(message = "last name must be entered")
    private String lastName;
    private Date birthDate;
    private String phoneNumber;
    private String sex;
    private String street;
    private String zipCode;
    private String country;
    private String city;
}
