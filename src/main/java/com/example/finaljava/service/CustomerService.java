package com.example.finaljava.service;

import com.example.finaljava.configuration.FileUploadUtil;
import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Customer;
import com.example.finaljava.repository.CustomerRepository;
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
public class CustomerService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.address}")
    private String localhost;

    @Value("${server.port}")
    private String port;

    @Autowired
    private CustomerRepository customerRepository;

    // ------------------- HELPER -------------------
    private String buildPhotoUrl(String photoFileName) {
        if (photoFileName == null || photoFileName.isEmpty()) return "";
        return "http://" + localhost + ":" + port + "/uploads/" + photoFileName;
    }

    private String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) return null;
        // Remove full URL if somehow stored
        return url.replaceFirst("^https?://.*/uploads/", "");
    }

    // ------------------- CREATE -------------------
    @Async
    @Transactional
    public Customer createCustomer(@Valid Customer customer, MultipartFile file) throws Exception {
        String fileName = null;
        try {
            if (file != null && !file.isEmpty()) {
                fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                customer.setPhoto(fileName); // store only filename
            } else {
                customer.setPhoto(null);
            }

            Customer savedCustomer = customerRepository.save(customer);

            if (fileName != null) {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            }

            // Set full URL before returning to frontend
            savedCustomer.setPhoto(buildPhotoUrl(savedCustomer.getPhoto()));
            return savedCustomer;

        } catch (Exception e) {
            if (fileName != null) {
                FileUploadUtil.removePhoto(uploadDir, fileName);
            }
            throw e;
        }
    }

    // ------------------- READ -------------------
    public List<Customer> getAllCustomers() {
        var customers = customerRepository.findAll();
        for (Customer c : customers) {
            // convert filename to full URL
            c.setPhoto(buildPhotoUrl(getFileNameFromUrl(c.getPhoto())));
        }
        return customers;
    }

    public Customer findCustomerById(int id) {
        var customer = customerRepository.findCustomerById(id);
        if (customer == null) {
            throw new MyResourceNotFoundException("Customer not found with id " + id);
        }
        customer.setPhoto(buildPhotoUrl(getFileNameFromUrl(customer.getPhoto())));
        return customer;
    }

    // ------------------- UPDATE -------------------
    @Transactional
    @Async
    public Customer updateCustomer(int id, @Valid Customer customer, MultipartFile file) throws IOException {
        var customerExists = customerRepository.findCustomerById(id);
        if (customerExists == null) {
            throw new MyResourceNotFoundException("Customer not found with id " + id);
        }

        // Update text fields
        customerExists.setName(customer.getName());
        customerExists.setEmail(customer.getEmail());
        customerExists.setGender(customer.getGender());
        customerExists.setTel(customer.getTel());
        customerExists.setAddress(customer.getAddress());

        if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
            customerExists.setPassword(customer.getPassword());
        }

        String newFileName = null;
        String oldPhotoFileName = getFileNameFromUrl(customerExists.getPhoto());

        try {
            if (file != null && !file.isEmpty()) {
                newFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                customerExists.setPhoto(newFileName); // store only filename
            }

            Customer savedCustomer = customerRepository.save(customerExists);

            if (newFileName != null) {
                FileUploadUtil.saveFile(uploadDir, newFileName, file);
                if (oldPhotoFileName != null && !oldPhotoFileName.isEmpty()) {
                    FileUploadUtil.removePhoto(uploadDir, oldPhotoFileName);
                }
            }

            // Return full URL to frontend
            savedCustomer.setPhoto(buildPhotoUrl(savedCustomer.getPhoto()));
            return savedCustomer;

        } catch (Exception e) {
            if (newFileName != null) {
                FileUploadUtil.removePhoto(uploadDir, newFileName);
            }
            throw e;
        }
    }

    // ------------------- DELETE -------------------
    @Transactional
    public void deleteCustomer(int id) throws IOException {
        var customerExists = customerRepository.findCustomerById(id);
        if (customerExists == null) {
            throw new MyResourceNotFoundException("Customer not found with id " + id);
        }

        String photoFileName = getFileNameFromUrl(customerExists.getPhoto());
        if (photoFileName != null && !photoFileName.isEmpty()) {
            FileUploadUtil.removePhoto(uploadDir, photoFileName);
        }

        customerRepository.deleteById(id);
    }

    // ------------------- PAGINATION -------------------
    public List<Customer> paginated(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var list = customerRepository.findAll(pageable).getContent();
        for (Customer c : list) {
            c.setPhoto(buildPhotoUrl(getFileNameFromUrl(c.getPhoto())));
        }
        return list;
    }

    // ------------------- SEARCH -------------------
    public List<Customer> searchByName(String name, int page, int size) {
        var pageable = PageRequest.of(page, size);
        var list = customerRepository.findByNameContainingIgnoreCase(name, pageable).getContent();
        for (Customer c : list) {
            c.setPhoto(buildPhotoUrl(getFileNameFromUrl(c.getPhoto())));
        }
        return list;
    }
}
