package com.example.t04.repository;

import com.example.t04.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Owner findByName(String ownerName);
}
