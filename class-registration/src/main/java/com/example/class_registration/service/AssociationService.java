package com.example.class_registration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.class_registration.exception.DuplicateResourceException;
import com.example.class_registration.exception.ResourceNotFoundException;
import com.example.class_registration.model.Associations;
import com.example.class_registration.repository.AssociationRepository;

@Service
public class AssociationService {

    @Autowired
    private AssociationRepository associationRepository;

    public List<Associations> getAllAssociations() {
        return associationRepository.findAll();
    }

    public Associations getAssociationById(Long id) {
        return associationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Association not found with id: " + id));
    }

    public Associations createAssociation(Associations association) {
        if (associationRepository.existsByEmail(association.getEmail())) {
            throw new DuplicateResourceException("Association already exists with email: " + association.getEmail());
        }

        if (associationRepository.existsByGroupName(association.getGroupName())) {
            throw new DuplicateResourceException("Association already exists with group name: " + association.getGroupName());
        }

        return associationRepository.save(association);
    }
}
