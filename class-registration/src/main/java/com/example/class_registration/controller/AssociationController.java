package com.example.class_registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.class_registration.model.Associations;
import com.example.class_registration.service.AssociationService;

@Controller
@RequestMapping("/associations")
public class AssociationController {

    @Autowired
    private AssociationService associationService;

    @GetMapping("/add")
    public String addAssociationPage(Model model) {
        model.addAttribute("association", new Associations());
        model.addAttribute("associations", associationService.getAllAssociations());
        return "add-association";
    }

    @PostMapping("/create")
    public String createAssociation(@ModelAttribute Associations association, Model model) {
        try {
            associationService.createAssociation(association);
            model.addAttribute("association", new Associations());
            model.addAttribute("associations", associationService.getAllAssociations());
            model.addAttribute("success", "Association created successfully.");
            return "add-association";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("association", association);
            model.addAttribute("associations", associationService.getAllAssociations());
            return "add-association";
        }
    }

    @GetMapping("/{id}/edit")
    public String editAssociationPage(@PathVariable Long id, Model model) {
        model.addAttribute("association", associationService.getAssociationById(id));
        model.addAttribute("associations", associationService.getAllAssociations());
        return "edit-association";
    }

    @PostMapping("/{id}/update")
    public String updateAssociation(@PathVariable Long id, @ModelAttribute Associations association, Model model) {
        try {
            associationService.updateAssociation(id, association);
            model.addAttribute("association", new Associations());
            model.addAttribute("associations", associationService.getAllAssociations());
            model.addAttribute("success", "Association updated successfully.");
            return "add-association";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("association", association);
            model.addAttribute("associations", associationService.getAllAssociations());
            return "edit-association";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteAssociation(@PathVariable Long id, Model model) {
        try {
            associationService.deleteAssociation(id);
            model.addAttribute("association", new Associations());
            model.addAttribute("associations", associationService.getAllAssociations());
            model.addAttribute("success", "Association deleted successfully.");
            return "add-association";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("associations", associationService.getAllAssociations());
            return "add-association";
        }
    }
}
