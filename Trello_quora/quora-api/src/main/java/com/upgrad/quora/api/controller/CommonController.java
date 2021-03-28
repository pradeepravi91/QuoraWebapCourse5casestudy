package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserCommonService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {


    @Autowired
    private UserCommonService userCommonssService;


    /*This endpoint is a GET request and is used to get details of any user in the Application
    it throws an AuthorizationFailedException, if the access token provided by the user doesn't exist or throws a UserNotFoundException,
    if the UUID of the user to be retrieved doesn't exist in*/

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity < UserDetailsResponse > userProfile(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        String Token = null;
        try {
            Token = accessToken.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            Token = accessToken;
        }
        final UserEntity userEntity = userCommonssService.getUser(userUuid, Token);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName()).userName(userEntity.getUserName()).emailAddress(userEntity.getEmail())
                .country(userEntity.getCountry()).aboutMe(userEntity.getAboutMe()).dob(userEntity.getDob()).contactNumber(userEntity.getContactNumber());
        return new ResponseEntity < UserDetailsResponse > (userDetailsResponse, HttpStatus.OK);
    }

}