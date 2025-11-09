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

    // ------------------- CREATE -------------------
    @Async
    @Transactional
    public Customer createCustomer(@Valid Customer customer, MultipartFile file) throws Exception {
        String fileName = null;

        try {
            if (file != null && !file.isEmpty()) {
                fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                customer.setPhoto(fileName);
            } else {
                customer.setPhoto(null);
            }

            Customer savedCustomer = customerRepository.save(customer);

            if (fileName != null) {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            }

            return savedCustomer;
        } catch (Exception e) {
            if (fileName != null) {
                FileUploadUtil.removePhoto(uploadDir, fileName);
            }
            throw e;
        }
    }

    // Helper to build full photo URL
    private String buildPhotoUrl(String photoFileName) {
        if(photoFileName == null || photoFileName.isEmpty()) return "";
        return "http://" + localhost + ":" + port + "/uploads/" + photoFileName;
    }

    public List<Customer> getAllCustomers() {
        var customers = customerRepository.findAll();
        for (Customer c : customers) {
            c.setPhoto(buildPhotoUrl(c.getPhoto()));
        }
        return customers;
    }

    public Customer findCustomerById(int id) {
        var customer = customerRepository.findCustomerById(id);
        if (customer == null) {
            throw new MyResourceNotFoundException("Customer not found with id " + id);
        }
        customer.setPhoto(buildPhotoUrl(customer.getPhoto()));
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

        // Update password only if provided
        if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
            customerExists.setPassword(customer.getPassword());
        }

        String newFileName = null;
        String oldPhoto = customerExists.getPhoto();

        try {
            if (file != null && !file.isEmpty()) {
                newFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                customerExists.setPhoto(newFileName);
            }

            Customer savedCustomer = customerRepository.save(customerExists);

            if (newFileName != null) {
                FileUploadUtil.saveFile(uploadDir, newFileName, file);
                if (oldPhoto != null && !oldPhoto.isEmpty()) {
                    FileUploadUtil.removePhoto(uploadDir, oldPhoto);
                }
            }

            // Build full photo URL before returning
            if (savedCustomer.getPhoto() != null && !savedCustomer.getPhoto().isEmpty()) {
                savedCustomer.setPhoto("http://" + localhost + ":" + port + "/uploads/" + savedCustomer.getPhoto());
            } else {
                savedCustomer.setPhoto("");
            }

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

        if (customerExists.getPhoto() != null && !customerExists.getPhoto().isEmpty()) {
            FileUploadUtil.removePhoto(uploadDir, customerExists.getPhoto());
        }

        this.customerRepository.deleteById(id);
    }

    // ------------------- PAGINATION -------------------
    public List<Customer> paginated(int page, int size) {
        var pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable).getContent();
    }

    // ------------------- SEARCH -------------------
    public List<Customer> searchByName(String name, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return customerRepository.findByNameContainingIgnoreCase(name, pageable).getContent();
    }
}
