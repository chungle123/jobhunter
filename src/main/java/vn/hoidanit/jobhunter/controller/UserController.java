package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPagination;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.errors.IdInvalidException;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("created user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {

        boolean isCheck = this.userService.checkByEmail(user.getEmail());
        if (isCheck) {
            throw new IdInvalidException("User này đã tồn tại");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUser(newUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("deleted user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.fetchById(id);
        if (user == null) {
            throw new IdInvalidException("User có id : " + id + " không tồn tại !");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetched user by id")
    public ResponseEntity<ResUserDTO> fetchUserById(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.fetchById(id);
        if (user == null) {
            throw new IdInvalidException("User có id : " + id + " không tồn tại !");
        }
        return ResponseEntity.ok(this.userService.convertToResUser(user));
    }

    @GetMapping("/users")
    @ApiMessage("fetched all user")
    public ResponseEntity<ResultPagination> fetchAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.userService.fetchAll(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("updated user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User userUpdate = this.userService.handleUpdate(user);
        if (userUpdate == null) {
            throw new IdInvalidException("User có id : " + user.getId() + " không tồn tại !");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUser(userUpdate));
    }
}
