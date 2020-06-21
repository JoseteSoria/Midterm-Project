package com.ironhack.MidtermProject.controller.impl.user;

import com.ironhack.MidtermProject.controller.interfaces.user.ThirdPartyController;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.service.user.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ThirdPartyControllerImpl implements ThirdPartyController {
    @Autowired
    private ThirdPartyService thirdPartyService;

    @GetMapping("/thirdParties")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ThirdParty> findAll(){ return thirdPartyService.findAll(); }
}
