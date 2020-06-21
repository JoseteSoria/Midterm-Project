package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThirdPartyService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    public List<ThirdParty> findAll(){ return thirdPartyRepository.findAll(); }
}
