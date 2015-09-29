package org.devgateway.importtool.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Optional;

import org.devgateway.importtool.dao.UserRepository;
import org.devgateway.importtool.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/users")
class UserController {

    private final UserRepository repository;

    @Autowired
    UserController(UserRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user}")
    ResponseEntity<User> deleteUser(@PathVariable Long user) {
    	
    	User userObject = repository.findOne((Long) user);
    	repository.delete(userObject);
        return new ResponseEntity<>(userObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = GET, value = "/{user}")
    ResponseEntity<User> loadUser(@PathVariable Long user) {
        return Optional.of(repository.findById(user))
                .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
