package com.thesis.gama.service;

import com.thesis.gama.dto.BrandSetDTO;
import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.exceptions.ExistingForeignKeysException;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.model.Brand;
import com.thesis.gama.model.Product;
import com.thesis.gama.repository.BrandRepository;
import com.thesis.gama.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public BrandService(BrandRepository brandRepository, ProductRepository productRepository) {
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
    }

    public Brand findById (int id) throws NoDataFoundException {

        return brandRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException("There's no brand with id" + id));
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }


    public void createBrand (BrandSetDTO brandSetDTO) throws AlreadyExistsException {

        if(brandRepository.findByName(brandSetDTO.getName()).isPresent()){
            throw new AlreadyExistsException ("There's a Brand with that name");
        } else {
            this.brandRepository.save(Brand.builder().name(brandSetDTO.getName()).build());
        }

    }


    public void deleteBrand(int brandId) throws NoDataFoundException, ExistingForeignKeysException {

        if(existsById(brandId)) {
            Optional<Product> product = productRepository.findProductByBrand(brandId);
            if (product.isPresent()) {
                throw new ExistingForeignKeysException("There are still products associated with that Brand");
            } else {
                this.brandRepository.deleteById(brandId);
            }
        } else {
            throw new NoDataFoundException("There's no brand with that id " + brandId);
        }
    }

    public boolean existsById(int id) {
        return brandRepository.existsById(id);
    }
}


