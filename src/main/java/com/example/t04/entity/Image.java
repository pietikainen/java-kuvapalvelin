package com.example.t04.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Lob
    @Column(name = "content", columnDefinition = "LONGBLOB")
    private byte[] content;
    @Column
    private String contentType;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner ownerId;

    public Image() {}
    public Image(String name, byte[] content, String contentType, Owner ownerId) {
        this.name = name;
        this.content = content;
        this.contentType = contentType;
        this.ownerId = ownerId;
    }

    public Image(String name, byte[] content, String contentType) {
        this.name = name;
        this.content = content;
        this.contentType = contentType;
    }

    // getters and setters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        if (isValidImageContentType(contentType)) {
            this.contentType = contentType;
        } else {
            throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
    }
    public Owner getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Owner ownerId) {
        this.ownerId = ownerId;
    }
    public void setOwner(Owner existingOwner) {
        this.ownerId = existingOwner;
    }

    private boolean isValidImageContentType(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
}
