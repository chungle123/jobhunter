package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPagination;

import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;

    public UserService(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> comOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(comOptional.isPresent() ? comOptional.get() : null);
        }
        return userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResultPagination fetchAll(Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);
        ResultPagination rs = new ResultPagination();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        rs.setMeta(meta);
        List<ResUserDTO> users = page.getContent().stream()
                .map(item -> new ResUserDTO(item.getId(), item.getName(), item.getEmail(), item.getAge(),
                        item.getGender(), item.getAddress(), item.getCreatedAt(), item.getUpdatedAt(),
                        new ResUserDTO.CompanyUser(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null)))
                .collect(Collectors.toList());
        rs.setResult(users);
        return rs;
    }

    public User handleUpdate(User userReq) {
        User user = this.fetchById(userReq.getId());
        if (user != null) {
            user.setGender(userReq.getGender());
            user.setAddress(userReq.getAddress());
            user.setName(userReq.getName());
            user.setAge(userReq.getAge());
            if (userReq.getCompany() != null) {
                Optional<Company> companyOptional = this.companyService.findById(userReq.getCompany().getId());
                user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            user = this.userRepository.save(user);
        }
        return user;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean checkByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUser(User newUser) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();
        if (newUser.getCompany() != null) {
            com.setId(newUser.getCompany().getId());
            com.setName(newUser.getCompany().getName());
            res.setCompany(com);
        }
        res.setId(newUser.getId());
        res.setName(newUser.getName());
        res.setEmail(newUser.getEmail());
        res.setGender(newUser.getGender());
        res.setAddress(newUser.getAddress());
        res.setAge(newUser.getAge());
        res.setCreatedAt(newUser.getCreatedAt());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUser(User userUpdate) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();
        if (userUpdate.getCompany() != null) {
            com.setId(userUpdate.getCompany().getId());
            com.setName(userUpdate.getCompany().getName());
            res.setCompany(com);
        }
        res.setId(userUpdate.getId());
        res.setAddress(userUpdate.getAddress());
        res.setAge(userUpdate.getAge());
        res.setGender(userUpdate.getGender());
        res.setName(userUpdate.getName());
        res.setUpdatedAt(userUpdate.getUpdatedAt());
        return res;
    }

    public ResUserDTO convertToResUser(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    public void updateRefreshToken(String token, String email) {
        User user = this.handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
