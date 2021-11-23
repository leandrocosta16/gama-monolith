package com.thesis.gama.service;

import com.thesis.gama.dto.CategorySetDTO;
import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.exceptions.ExistingForeignKeysException;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.model.Category;
import com.thesis.gama.model.Product;
import com.thesis.gama.repository.CategoryRepository;
import com.thesis.gama.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;


    public Category findById (int id) throws NoDataFoundException {

        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException("There is no Category with id" + id));
    }


    public void createCategory(CategorySetDTO category) throws AlreadyExistsException {
        if(categoryRepository.findByName(category.getName()).isPresent()){
            throw new AlreadyExistsException ("There's a Category with that name");
        } else {
            this.categoryRepository.save(new Category(category));
        }
    }

    public void deleteCategory(int catId) throws NoDataFoundException, ExistingForeignKeysException {

        if(existsById(catId)) {
            //existingBrand.ifPresent(brand -> this.brandRepository.delete(brand));
            Optional<Product> product = productRepository.findProductByCategory(catId);
            if (product.isPresent()) {
                throw new ExistingForeignKeysException("There are still products associated with that Category");
            } else {
                this.categoryRepository.deleteById(catId);
            }
        } else {
            throw new NoDataFoundException("There's no category with that id " + catId);
        }
    }


    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public boolean existsById(int id) {
        return categoryRepository.existsById(id);
    }
}