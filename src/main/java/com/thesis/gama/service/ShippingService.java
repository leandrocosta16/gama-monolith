package com.thesis.gama.service;

import com.thesis.gama.dto.ShippingReferenceSetDTO;
import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.model.ShippingReferenceValue;
import com.thesis.gama.repository.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Service
public class ShippingService {

    @Autowired
    ShippingRepository shippingRepository;

    public Double calculateShippingValue(float weight, String country) throws NoDataFoundException {
        Optional<ShippingReferenceValue> s = shippingRepository.findByCountry(country);
        if(s.isPresent()) {
            return s.get().getCostPerkg()*weight;
        }
        else {
            throw new NoDataFoundException("We don't ship to " + country);
        }
    }


    public void createShippingReference(ShippingReferenceSetDTO shippingReferenceSetDTO) throws AlreadyExistsException {
        if(shippingRepository.findByCountry(shippingReferenceSetDTO.getCountry()).isEmpty()) {
            ShippingReferenceValue s1 = new ShippingReferenceValue(shippingReferenceSetDTO);
            shippingRepository.save(s1);
        } else {
            throw new AlreadyExistsException("That country is already registered");
        }
    }

}
