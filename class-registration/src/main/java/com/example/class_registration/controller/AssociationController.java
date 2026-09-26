package com.example.class_registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        return "add-association";
    }

    @PostMapping("/create")
    public String createAssociation(@ModelAttribute Associations association, Model model) {
        try {
            associationService.createAssociation(association);
            return "redirect:/instructor/home?associationAdded=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("association", association);
            return "add-association";
        }
    }
}
