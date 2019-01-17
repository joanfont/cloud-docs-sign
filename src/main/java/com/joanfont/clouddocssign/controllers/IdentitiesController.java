package com.joanfont.clouddocssign.controllers;


import com.joanfont.clouddocssign.lib.PersistentFlashBag;
import com.joanfont.clouddocssign.trustedx.Scope;
import com.joanfont.clouddocssign.trustedx.TokenRequest;
import com.joanfont.clouddocssign.trustedx.TrustedXOAuthClient;
import com.joanfont.clouddocssign.trustedx.TrustedXTokenRepository;
import com.joanfont.clouddocssign.trustedx.entities.Identity;
import com.joanfont.clouddocssign.trustedx.entities.Role;
import com.joanfont.clouddocssign.trustedx.entities.Token;
import com.joanfont.clouddocssign.trustedx.entities.TrustedXCredentials;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXTokenNotFoundException;
import com.joanfont.clouddocssign.trustedx.exceptions.TrustedXUnauthorizedException;
import com.joanfont.clouddocssign.trustedx.requests.RegisterIdentityRequest;
import com.joanfont.clouddocssign.trustedx.requests.RemoveIdentityRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/identities")
public class IdentitiesController {

    private TrustedXOAuthClient client;

    private TrustedXTokenRepository tokenRepository;

    private PersistentFlashBag flashBag;

    private static final Role[] AVAILABLE_ROLES = {
            Role.STUDENT,
            Role.TEACHER
    };

    private static final String[] DEFAULT_LABELS = {"uoc"};

    public IdentitiesController(
            TrustedXTokenRepository tokenRepository,
            TrustedXCredentials credentials,
            PersistentFlashBag flashbag
    ) {
        this.tokenRepository = tokenRepository;
        this.client = new TrustedXOAuthClient(credentials, tokenRepository);
        this.flashBag = flashbag;
    }

    @GetMapping("")
    public String list(Model model) throws TrustedXException {
        try {
            List<Identity> identities = this.client.listIdentities();

            model.addAttribute("identities", identities);
            return "identities/list";
        } catch (TrustedXUnauthorizedException | TrustedXTokenNotFoundException ex) {
            return this.retirectToTokenRetrival("/identities", Scope.MANAGE_IDENTITY);
        }
    }

    @GetMapping("/add")
    public String add(Model model) {
        Scope requiredScope = Scope.REGISTER_IDENTITY;

        try {
            Token token = this.tokenRepository.get(requiredScope);
            if (token.isExpired()) {
                return this.retirectToTokenRetrival("/identities/add", requiredScope);
            }
        } catch (TrustedXTokenNotFoundException ex) {
            return this.retirectToTokenRetrival("/identities/add", requiredScope);
        }

        model.addAttribute("roles", AVAILABLE_ROLES);
        return "identities/add";
    }

    @PostMapping("/add")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password,
            @RequestParam("role") String role
    ) throws Exception {
        String base64Pkcs12 = Base64.getEncoder().encodeToString(file.getBytes());
        List<String> userLabels = new ArrayList<>(Arrays.asList(DEFAULT_LABELS));
        userLabels.add(role);

        Identity identity = new Identity(base64Pkcs12, password, userLabels);
        RegisterIdentityRequest request = new RegisterIdentityRequest();
        request.setIdentity(identity);

        this.client.registerIdentity(request);
        return "redirect:/identities";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") String id) throws TrustedXException {
        Identity identity = new Identity();
        identity.setId(id);

        RemoveIdentityRequest request = new RemoveIdentityRequest();
        request.setIdentity(identity);

        try {
            this.client.removeIdentity(request);
        } catch (TrustedXTokenNotFoundException | TrustedXUnauthorizedException ex) {
            this.retirectToTokenRetrival("/delete/" + identity.getId(), Scope.MANAGE_IDENTITY);
        }

        return "redirect:/identities";
    }

    private String retirectToTokenRetrival(String returnUrl, Scope scope) {
        TokenRequest tokenRequest = new TokenRequest(returnUrl, scope);
        this.flashBag.add("token_request", tokenRequest);

        return "forward:/trustedx/login";
    }
}
