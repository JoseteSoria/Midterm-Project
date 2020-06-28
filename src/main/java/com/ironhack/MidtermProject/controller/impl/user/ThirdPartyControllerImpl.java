package com.ironhack.MidtermProject.controller.impl.user;

import com.ironhack.MidtermProject.controller.interfaces.user.ThirdPartyController;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.service.user.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ThirdPartyControllerImpl implements ThirdPartyController {
    @Autowired
    private ThirdPartyService thirdPartyService;

    @GetMapping("/third-parties")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ThirdParty> findAll() {
        return thirdPartyService.findAll();
    }

    @PostMapping("/third-parties")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ThirdParty create(@RequestBody ThirdParty thirdParty) {
        return thirdPartyService.store(thirdParty);
    }
}
