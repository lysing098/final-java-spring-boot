package com.example.finaljava.service;

import com.example.finaljava.configuration.FileUploadUtil;
import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Role;
import com.example.finaljava.model.User;
import com.example.finaljava.repository.RoleRepository;
import com.example.finaljava.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.address}")
    private String localhost;

    @Value("${server.port}")
    private String port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository; // ✅ Needed for role lookup

    // ------------------- CREATE -------------------
    @Async
    @Transactional
    public User createUser(@Valid User user, MultipartFile file) throws Exception {
        String fileName = null;

        try {
            // ✅ Validate and set role from DB
            if (user.getRole() != null && user.getRole().getId() != 0) {
                Role role = roleRepository.findById(user.getRole().getId())
                        .orElseThrow(() -> new MyResourceNotFoundException("Role not found with id: " + user.getRole().getId()));
                user.setRole(role);
            }

            if (file != null && !file.isEmpty()) {
                fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                user.setPhoto(fileName);
            } else {
                user.setPhoto(null);
            }

            User savedUser = userRepository.save(user);

            // Save file physically only after DB success
            if (fileName != null) {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            }

            return savedUser;
        } catch (Exception e) {
            // Rollback: if something failed after file saved, remove file
            if (fileName != null) {
                FileUploadUtil.removePhoto(uploadDir, fileName);
            }
            throw e;
        }
    }

    // ------------------- GET ALL -------------------
    public List<User> getAllUsers() {
        var users = userRepository.findAll();
        for (User user : users) {
            if (user.getPhoto() == null || user.getPhoto().isEmpty()) {
                user.setPhoto("");
            } else {
                user.setPhoto("http://" + localhost + ":" + port + "/uploads/" + user.getPhoto());
            }
        }
        return users;
    }


    // ------------------- GET BY ID -------------------
    public User findUserById(int id) {
        var user = userRepository.findUserById(id);
        if (user == null) {
            throw new MyResourceNotFoundException("User not found with id " + id);
        }

        if (user.getPhoto() == null || user.getPhoto().isEmpty()) {
            user.setPhoto("");
        } else {
            user.setPhoto("http://" + localhost + ":" + port + "/uploads/" + user.getPhoto());
        }
        return user;
    }


    // ------------------- UPDATE -------------------
    @Transactional
    @Async
    public User updateUser(int id, @Valid User user, MultipartFile file) throws IOException {
        var userExists = userRepository.findUserById(id);
        if (userExists == null) {
            throw new MyResourceNotFoundException("User not found with id " + id);
        }

        // ✅ Update text fields
        userExists.setName(user.getName());
        userExists.setEmail(user.getEmail());
        userExists.setGender(user.getGender());
        userExists.setPassword(user.getPassword());
        userExists.setTel(user.getTel());
        userExists.setAddress(user.getAddress());

        // ✅ Update Role safely
        if (user.getRole() != null && user.getRole().getId() != 0) {
            Role role = roleRepository.findById(user.getRole().getId())
                    .orElseThrow(() -> new MyResourceNotFoundException("Role not found with id: " + user.getRole().getId()));
            userExists.setRole(role);
        }

        String newFileName = null;
        String oldPhoto = userExists.getPhoto();

        try {
            if (file != null && !file.isEmpty()) {
                newFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                userExists.setPhoto(newFileName);
            }

            User savedUser = userRepository.save(userExists);

            // ✅ Only save new file after DB success
            if (newFileName != null) {
                FileUploadUtil.saveFile(uploadDir, newFileName, file);

                // Remove old photo after success
                if (oldPhoto != null && !oldPhoto.isEmpty()) {
                    FileUploadUtil.removePhoto(uploadDir, oldPhoto);
                }
            }

            return savedUser;
        } catch (Exception e) {
            // Cleanup new file if something failed
            if (newFileName != null) {
                FileUploadUtil.removePhoto(uploadDir, newFileName);
            }
            throw e;
        }
    }

    // ------------------- DELETE -------------------
    @Transactional
    public void deleteUser(int id) throws IOException {
        var userExists = userRepository.findUserById(id);
        if (userExists == null) {
            throw new MyResourceNotFoundException("User not found with id " + id);
        }

        if (userExists.getPhoto() != null && !userExists.getPhoto().isEmpty()) {
            FileUploadUtil.removePhoto(uploadDir, userExists.getPhoto());
        }

        this.userRepository.deleteById(id);
    }

    // ------------------- PAGINATION -------------------
    public List<User> paginated(int page, int size) {
        var pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).getContent();
    }

    // ------------------- SEARCH -------------------
    public List<User> searchByName(String name, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return userRepository.findByNameContainingIgnoreCase(name, pageable).getContent();
    }
}
