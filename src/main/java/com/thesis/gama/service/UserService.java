package com.thesis.gama.service;

import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.security.JwtTokenUtil;
import com.thesis.gama.dto.UserGetDTO;
import com.thesis.gama.dto.UserSetDTO;
import com.thesis.gama.model.Account;
import com.thesis.gama.model.Address;
import com.thesis.gama.model.Role;
import com.thesis.gama.model.User;
import com.thesis.gama.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder bcryptEncoder;


    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByAccount_Email(email);
    }

    public boolean emailAlreadyExists(String email) {
        return findByEmail(email).isPresent();
    }

    public UserGetDTO getMyUserDetails(String authorizationToken) {
        String email = jwtTokenUtil.getEmailFromAuthorizationString(authorizationToken);
            return new UserGetDTO(findByEmail(email).get());
    }
    public User getMyUser(String authorizationToken) {
        String email = jwtTokenUtil.getEmailFromAuthorizationString(authorizationToken);
        return findByEmail(email).get();
    }

    public UserGetDTO getUserById(int id) throws NoDataFoundException {
        Optional<User> user = this.userRepository.findById(id);
        if(user.isPresent()) {
            return new UserGetDTO(user.get());
        }
        else {
            throw new NoDataFoundException("There's no User with that id");
        }
    }

    public UserGetDTO createUser(UserSetDTO userSetDto) throws AlreadyExistsException {

        Optional<User> userOptional = findByEmail(userSetDto.getEmail());
        if (userOptional.isPresent()){
            throw new AlreadyExistsException ("There's a user with that email");
        }
        else {
            Account account = new Account(userSetDto.getEmail(), bcryptEncoder.encode(userSetDto.getPassword()),Role.USER);
            Address address = new Address(userSetDto.getStreet(), userSetDto.getZipCode(), userSetDto.getCountry(), userSetDto.getCity());
            User createdUser = new User(userSetDto, account);
            createdUser.addAddressToUser(address);

            userRepository.save(createdUser);

            return new UserGetDTO(createdUser);
        }
    }


    public UserGetDTO createAdmin(UserSetDTO userSetDto) throws AlreadyExistsException {

        Optional<User> userOptional = findByEmail(userSetDto.getEmail());
        if (userOptional.isPresent()){
            throw new AlreadyExistsException ("There's a user with that email");
        }
        else {
            Account account = new Account(userSetDto.getEmail(), bcryptEncoder.encode(userSetDto.getPassword()),Role.ADMIN);
            Address address = new Address(userSetDto.getStreet(), userSetDto.getZipCode(), userSetDto.getCountry(), userSetDto.getCity());
            User createdUser = new User(userSetDto, account);
            createdUser.addAddressToUser(address);

            userRepository.save(createdUser);

            return new UserGetDTO(createdUser);
        }
    }
}
