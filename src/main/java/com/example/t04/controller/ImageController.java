package com.example.t04.controller;

import com.example.t04.entity.Image;
import com.example.t04.entity.Owner;
import com.example.t04.repository.ImageRepository;
import com.example.t04.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class ImageController {

    ImageRepository imageRepository;
    OwnerRepository ownerRepository;

    @Autowired
    public ImageController(ImageRepository imageRepository, OwnerRepository ownerRepository) {
        this.imageRepository = imageRepository;
        this.ownerRepository = ownerRepository;
    }

    // Front page
    @GetMapping("/")
    public String showImages(Model model) {
        List<Image> images = imageRepository.findAll();
        model.addAttribute("images", images);
        return "index";
    }

    // Basic text listing of images
    @GetMapping("/images")
    public String index(Model model) {
        List<Image> images = imageRepository.findAll();
        model.addAttribute("images", images);
        return "filelist";
    }

    // Image display
    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Optional<Image> optionalImage = imageRepository.findById(id);
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(image.getContent().length);
            return new ResponseEntity<>(image.getContent(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Image upload
    @PostMapping("/images/add")
    public String addImage(@RequestParam("image") MultipartFile image, @RequestParam("ownerName") String ownerName, RedirectAttributes redirectAttributes) {
        try {
            // start with simple checks: file size, empty file, and file type
            //  1 MB max file size
            long maxFileSize = 1024*1024;

            // Check file size
            if (image.getSize() > maxFileSize) {
                redirectAttributes.addFlashAttribute("error", "File size exceeds maximum limit of 1 MB.");
                return "redirect:/";
            }

            // Check if file is empty
            if (image.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file.");
                return "redirect:/";
            }

            // Check if file is in approved image types list
            List<String> allowedContentTypes = Arrays.asList("image/jpeg", "image/jpg", "image/png");

            if (image.getContentType() == null || !allowedContentTypes.contains(image.getContentType().toLowerCase())) {
                redirectAttributes.addFlashAttribute("error", "Invalid image type.");
                return "redirect:/";
            }

            // Check existing owners from database
            Owner existingOwner = ownerRepository.findByName(ownerName);

            // Save owner if not found
            if (existingOwner == null) {
                Owner owner = new Owner();
                owner.setName(ownerName);
                ownerRepository.save(owner);
                existingOwner = owner;
            }

            // Save image
            Image img = new Image();
            img.setName(image.getOriginalFilename());
            img.setContentType(image.getContentType());
            img.setContent(image.getBytes());
            img.setOwner(existingOwner);
            System.out.println("Image getsize: " + image.getSize() + " bytes.");
            System.out.println("Image getcontent.length: " + img.getContent().length / 1024 + " KB.");
            imageRepository.save(img);

            redirectAttributes.addFlashAttribute("success", "Image saved to database.");
            return "redirect:/";
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing the image.");
            return "redirect:/";
        }
    }

    // Image download
    @GetMapping("/images/download/{id}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) {
        Optional<Image> optionalImage = imageRepository.findById(id);
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(image.getContent().length);
            headers.setContentDispositionFormData("attachment", image.getName());

            return new ResponseEntity<>(image.getContent(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Image delete
    @PostMapping("/images/delete/{id}")
    public String deleteImage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            imageRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("error", "Image deleted from database.");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the image.");
            return "redirect:/";
        }
    }
}
