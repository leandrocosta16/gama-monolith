package com.thesis.gama.service;

import com.thesis.gama.dto.*;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.exceptions.PromotionConflictException;
import com.thesis.gama.model.*;
import com.thesis.gama.repository.ProductRepository;
import com.thesis.gama.repository.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class ProductService {

    private final BrandService brandService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductService(BrandService brandService, CategoryService categoryService, UserService userService, ProductRepository productRepository, ReviewRepository reviewRepository, ModelMapper modelMapper) {
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    private static final String PRODUCT_NOT_FOUND = "There is no product with id: ";

    public ProductDetailsGetDTO getProductDetailsById(int id) throws NoDataFoundException {
        Product product = this.getProductById(id);
        return new ProductDetailsGetDTO(product);
    }

    public Product getProductById(int id) throws NoDataFoundException {

        return productRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException(PRODUCT_NOT_FOUND + id));
    }

    public void deleteProduct(int id) throws NoDataFoundException {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new NoDataFoundException(PRODUCT_NOT_FOUND + id);
        }
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    public List<ProductGetDTO> getProducts(int page, int size, String sort, Specification<Product> specs) {

        String[] _sort = sort.split(",");

        Pageable paging = PageRequest.of(page, size, getSortDirection(_sort[0]), _sort[1]);

        List<ProductGetDTO> allProducts = new ArrayList<>();
        this.productRepository.findAll(Specification.where(specs), paging).forEach(product -> allProducts.add(new ProductGetDTO(product)));
        return allProducts;
    }


    public List<ProductDetailsAdminGetDTO> getProductsAdmin(int page, int size, String sort, Specification<Product> specs) {

        String[] _sort = sort.split(",");

        Pageable paging = PageRequest.of(page, size, getSortDirection(_sort[0]), _sort[1]);

        List<ProductDetailsAdminGetDTO> allProducts = new ArrayList<>();
        this.productRepository.findAll(Specification.where(specs), paging).forEach(product -> allProducts.add(new ProductDetailsAdminGetDTO(product)));
        return allProducts;
    }

    public ProductGetDTO createProduct(ProductSetDTO productSetDTO) throws NoDataFoundException {
        //hmm verifico se nome do produto já existe?
        //Optional<Product> existingProduct = this.carRepository.findByLicensePlate(carSetDto.getLicensePlate());
        Brand brand;
        Category category;

        brand = this.brandService.findById(productSetDTO.getBrandId());
        category = this.categoryService.findById(productSetDTO.getCategoryId());
        List<SpecificationValue> values = new ArrayList<>();
        productSetDTO.getSpecificationValues().forEach(value -> values.add(new SpecificationValue(value)));

        Product p = new Product(productSetDTO, brand, category);
        p.addSpecificationValuesToProduct(values);
        this.productRepository.save(p);

        return modelMapper.map(p, ProductGetDTO.class);

    }

    public List<ReviewGetDTO> getProductReviews(int id) throws NoDataFoundException {
        Product product = this.getProductById(id);
        List<ReviewGetDTO> reviews = new ArrayList<>();
        product.getReviews().forEach(review -> reviews.add(new ReviewGetDTO(review)));
        return reviews;

    }


    public ReviewGetDTO createReview(String authorizationToken, int productID, ReviewSetDTO reviewSetDTO) throws NoDataFoundException {
        User user = this.userService.getMyUser(authorizationToken);
        Product product = this.getProductById(productID);

        Review r = new Review(reviewSetDTO, user, product);
        this.reviewRepository.save(r);
        return modelMapper.map(r, ReviewGetDTO.class);
    }


    public void deleteReview(String authorizationToken, int reviewID) throws NoDataFoundException {
        User user = this.userService.getMyUser(authorizationToken);
        Optional<Review> review = this.reviewRepository.findById(reviewID);
        if (review.isPresent() && review.get().getUser().getId() == user.getId()) {
            reviewRepository.delete(review.get());
        } else {
            throw new NoDataFoundException("You cannot delete review " + reviewID);
        }
    }

    //I also need to call this method whenever the real price is edited
    public void setPromotion(Product product, Promotion promotion) throws PromotionConflictException {
        if (product.getPromotion() == null) {
            product.setPromotionPrice(product.getPrice() - (product.getPrice() * (promotion.getDiscountAmount()) / 100));
            product.setPromotion(promotion);
            productRepository.save(product);
        } else {
            throw new PromotionConflictException("Product " + product.getName() + " is already associated with promotion" + promotion.getName());
        }

    }

    public void setPromotionPrice(Product product) {
        product.setPromotionPrice(product.getPrice() * (product.getPromotion().getDiscountAmount()) / 100);
        productRepository.save(product);
    }

    public void resetPromotionAndPrice(Product product) {
        product.setPromotionPrice(null);
        product.setPromotion(null);
        productRepository.save(product);
    }

    public ProductGetDTO editProduct(Map<String, Object> updates, int id) throws NoDataFoundException {
        Product product = this.getProductById(id);
        try {
            // Map key is field name, v is value
            updates.forEach((k, v) -> {
                // use reflection to get field k on manager and set it to value v
                Field field = ReflectionUtils.findField(Product.class, k);
                field.setAccessible(true);
                ReflectionUtils.setField(field, product, v);
            });
            productRepository.save(product);
            return modelMapper.map(product, ProductGetDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoDataFoundException("Invalid arguments");
        }

    }


}
