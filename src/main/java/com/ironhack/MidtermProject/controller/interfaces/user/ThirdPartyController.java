package com.ironhack.MidtermProject.controller.interfaces.user;

import com.ironhack.MidtermProject.model.user.ThirdParty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public interface ThirdPartyController {

    public List<ThirdParty> findAll();

    public ThirdParty create(ThirdParty thirdParty);

}
