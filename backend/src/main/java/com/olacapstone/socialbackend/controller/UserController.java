package com.olacapstone.socialbackend.controller;

import java.security.Principal;
import java.util.Optional;

import com.olacapstone.socialbackend.entity.*;
import com.olacapstone.socialbackend.repository.UserRepository;
import com.olacapstone.socialbackend.service.JWTUtil;
import com.olacapstone.socialbackend.service.ResponseObjectService;
import com.olacapstone.socialbackend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/users")
    public ResponseEntity<ResponseObjectService> findAllUsers() {
        return new ResponseEntity<ResponseObjectService>(userService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/users/profile")
    public ResponseEntity<ResponseObjectService> findById(@RequestBody IdObjectEntity inputId) {
        return new ResponseEntity<ResponseObjectService>(userService.findById(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/follow")
    public ResponseEntity<ResponseObjectService> followUser(@RequestBody DoubleIdObjectEntity doubleId) {
        return new ResponseEntity<ResponseObjectService>(userService.followUser(doubleId), HttpStatus.OK);
    }

    @PostMapping("/users/unfollow")
    public ResponseEntity<ResponseObjectService> unfollowUser(@RequestBody DoubleIdObjectEntity doubleId) {
        return new ResponseEntity<ResponseObjectService>(userService.unfollowUser(doubleId), HttpStatus.OK);
    }

    @PostMapping("/users/getfollowing")
    public ResponseEntity<ResponseObjectService> findFollowing(@RequestBody IdObjectEntity inputId) {
        return new ResponseEntity<ResponseObjectService>(userService.findFollowing(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/getfollower")
    public ResponseEntity<ResponseObjectService> findFollower(@RequestBody IdObjectEntity inputId) {
        return new ResponseEntity<ResponseObjectService>(userService.findFollower(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/save")
    public ResponseEntity<ResponseObjectService> saveUser(@RequestBody UserEntity inputUser) {
        return new ResponseEntity<ResponseObjectService>(userService.saveUser(inputUser), HttpStatus.OK);
    }

    @PostMapping("/users/signin")
    public ResponseEntity<ResponseObjectService> userSignIn(@RequestBody UserSignInEntity inputUser) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(inputUser.getEmail(), inputUser.getPassword()));
            String token = jwtUtil.generateToken(inputUser.getEmail());
            
            Optional<UserEntity> optUser = userRepo.findByEmail(inputUser.getEmail());
            UserEntity user = optUser.get();
            user.setPassword("");
            return new ResponseEntity<ResponseObjectService>(new ResponseObjectService("success", "authenticated", new AuthorizedEntity(user, token)), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<ResponseObjectService>(new ResponseObjectService("fail", "unauthenticated", null), HttpStatus.OK);
        }
    }

    @PostMapping("/users/getallfirstname")
    public ResponseEntity<ResponseObjectService> getUserList(@RequestBody UserFindByNameEntity userFindByNameEntity){
        String firstName = userFindByNameEntity.getFirstName();
        return new ResponseEntity<ResponseObjectService>(userService.findAllByFirstName(firstName), HttpStatus.OK);
    }

    @PutMapping("/users/update")
    public ResponseEntity<ResponseObjectService> update(@RequestBody UserEntity inputUser) {
        return new ResponseEntity<ResponseObjectService>(userService.update(inputUser), HttpStatus.OK);
    }

    @GetMapping("/getdata")
    public ResponseEntity<String> testAfterLogin(Principal p) {
        return ResponseEntity.ok("Welcome. You are: " + p.getName());
    }
}
