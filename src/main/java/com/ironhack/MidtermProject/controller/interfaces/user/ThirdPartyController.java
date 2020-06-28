package com.ironhack.MidtermProject.controller.interfaces.user;

import com.ironhack.MidtermProject.model.user.ThirdParty;

import java.util.List;

public interface ThirdPartyController {

    public List<ThirdParty> findAll();

    public ThirdParty create(ThirdParty thirdParty);

}
