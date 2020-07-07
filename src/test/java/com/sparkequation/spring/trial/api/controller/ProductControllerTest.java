package com.sparkequation.spring.trial.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkequation.spring.trial.api.controller.exception.handler.ResponseErrorInfo;
import com.sparkequation.spring.trial.api.model.Product;
import com.sparkequation.spring.trial.api.service.ProductService;
import com.sparkequation.spring.trial.api.service.exception.NoSuchProductException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ProductControllerTest {

    private final static Integer PRODUCT_ID = 1;
    private final static Path PRODUCTS_PATH = Path.of("src", "test", "resources", "products");
    private final static Path RESPONSE_PATH = Path.of("src", "test", "resources", "expected", "response");
    private final static String NO_SUCH_PRODUCT_MSG = "No such Product";
    private final static String FAIL_TO_CONVERT_MSG = "Failed to convert value of type 'java.lang.String' "
                                                      + "to required type 'java.lang.Integer'; "
                                                      + "nested exception is java.lang.NumberFormatException: "
                                                      + "For input string: \"a\"";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    public void getProductsTest_ok() throws Exception {
        final List<Product> products = Collections.singletonList(new Product());
        when(productService.getProducts()).thenReturn(products);
        mockMvc.perform(get("/api/product/all"))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(products)));
        verify(productService).getProducts();
    }

    @Test
    public void getProductById_ok() throws Exception {
        final Product product = new Product();
        product.setId(PRODUCT_ID);
        when(productService.getProductById(eq(PRODUCT_ID))).thenReturn(product);
        mockMvc.perform(get("/api/product/{id}", PRODUCT_ID))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(product)));
        verify(productService).getProductById(PRODUCT_ID);
    }

    @Test
    public void getProductById_noSuchProduct() throws Exception {
        when(productService.getProductById(eq(PRODUCT_ID))).thenThrow(new NoSuchProductException(NO_SUCH_PRODUCT_MSG));
        mockMvc.perform(get("/api/product/{id}", PRODUCT_ID))
               .andExpect(status().isNotFound())
               .andExpect(result -> Assertions.assertEquals(
                       NO_SUCH_PRODUCT_MSG,
                       result.getResponse().getErrorMessage()
               ));
        verify(productService).getProductById(PRODUCT_ID);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void getProductById_wrongPathVariableType() throws Exception {
        mockMvc.perform(get("/api/product/{id}", "a"))
               .andExpect(status().isBadRequest())
               .andExpect(result -> Assertions.assertEquals(
                       FAIL_TO_CONVERT_MSG,
                       result.getResolvedException().getMessage()
               ));
        verify(productService, times(0)).getProductById(anyInt());
    }

    @Test
    public void getProductById_negativePathVariable() throws Exception {
        mockMvc.perform(get("/api/product/{id}", -1))
               .andExpect(status().isBadRequest())
               .andExpect(result -> Assertions.assertEquals(
                       "getProductById.id: Id must be positive.",
                       result.getResponse().getErrorMessage()
               ));
        verify(productService, times(0)).getProductById(anyInt());
    }

    @Test
    public void addProduct_ok() throws Exception {
        final Path productPath = PRODUCTS_PATH.resolve("CorrectProduct.json");
        final Product expectedProduct = objectMapper.readValue(productPath.toFile(), Product.class);
        mockMvc.perform(
                post("/api/product").content(Files.readAllBytes(productPath)).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
        verify(productService).addProduct(argThat(argument -> argument.equals(expectedProduct)));
    }

    @Test
    public void addProduct_emptyCategories() throws Exception {
        validateAddProduct("EmptyCategoriesProduct.json", "EmptyCategoriesResponse.json");
    }

    @Test
    public void addProduct_tooManyCategories() throws Exception {
        validateAddProduct("TooManyCategoriesProduct.json", "EmptyCategoriesResponse.json");
    }

    @Test
    public void addProduct_nullRequiredFields() throws Exception {
        validateAddProduct("NullRequiredFieldsProduct.json", "NullRequiredFieldsResponse.json");
    }

    @Test
    public void addProduct_incorrectExpirationDate() throws Exception {
        validateAddProduct("IncorrectExpirationDateProduct.json", "IncorrectExpirationDateResponse.json");
    }

    @Test
    public void addProduct_emptyName() throws Exception {
        validateAddProduct("EmptyNameProduct.json", "IncorrectNameResponse.json");
    }

    @Test
    public void addProduct_tooLongName() throws Exception {
        validateAddProduct("TooLongNameProduct.json", "IncorrectNameResponse.json");
    }

    @Test
    public void updateProductById_ok() throws Exception {
        final Path productPath = PRODUCTS_PATH.resolve("CorrectProduct.json");
        final Product expectedProduct = objectMapper.readValue(productPath.toFile(), Product.class);
        mockMvc.perform(
                put("/api/product/{id}", PRODUCT_ID)
                        .content(Files.readAllBytes(productPath))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
        verify(productService).updateProductById(eq(PRODUCT_ID), argThat(argument -> argument.equals(expectedProduct)));
    }

    @Test
    public void updateProductById_emptyCategories() throws Exception {
        validateUpdateProduct("EmptyCategoriesProduct.json", "EmptyCategoriesResponse.json");
    }

    @Test
    public void updateProductById_tooManyCategories() throws Exception {
        validateUpdateProduct("TooManyCategoriesProduct.json", "EmptyCategoriesResponse.json");
    }

    @Test
    public void updateProductById_nullRequiredFields() throws Exception {
        validateUpdateProduct("NullRequiredFieldsProduct.json", "NullRequiredFieldsResponse.json");
    }

    @Test
    public void updateProductById_incorrectExpirationDate() throws Exception {
        validateUpdateProduct("IncorrectExpirationDateProduct.json", "IncorrectExpirationDateResponse.json");
    }

    @Test
    public void updateProductById_emptyName() throws Exception {
        validateUpdateProduct("EmptyNameProduct.json", "IncorrectNameResponse.json");
    }

    @Test
    public void updateProductById_tooLongName() throws Exception {
        validateUpdateProduct("TooLongNameProduct.json", "IncorrectNameResponse.json");
    }

    @Test
    public void updateProductById_noSuchProduct() throws Exception {
        doThrow(new NoSuchProductException(NO_SUCH_PRODUCT_MSG)).when(productService)
                                                                .updateProductById(eq(PRODUCT_ID), any(Product.class));
        final Path productPath = PRODUCTS_PATH.resolve("CorrectProduct.json");
        final Product expectedProduct = objectMapper.readValue(productPath.toFile(), Product.class);

        mockMvc.perform(put("/api/product/{id}", PRODUCT_ID)
                                .content(Files.readAllBytes(productPath))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(result -> Assertions.assertEquals(
                       NO_SUCH_PRODUCT_MSG,
                       result.getResponse().getErrorMessage()
               ));
        verify(productService).updateProductById(eq(PRODUCT_ID), argThat(argument -> argument.equals(expectedProduct)));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void updateProductById_wrongPathVariableType() throws Exception {
        mockMvc.perform(put("/api/product/{id}", "a"))
               .andExpect(status().isBadRequest())
               .andExpect(result -> Assertions.assertEquals(
                       FAIL_TO_CONVERT_MSG,
                       result.getResolvedException().getMessage()
               ));
        verify(productService, times(0)).updateProductById(anyInt(), any(Product.class));
    }

    @Test
    public void updateProductById_negativePathVariable() throws Exception {
        final Path productPath = PRODUCTS_PATH.resolve("CorrectProduct.json");
        final Product expectedProduct = objectMapper.readValue(productPath.toFile(), Product.class);

        mockMvc.perform(put("/api/product/{id}", -1)
                                .content(Files.readAllBytes(productPath))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(result -> Assertions.assertEquals(
                       "updateProductById.id: Id must be positive.",
                       result.getResponse().getErrorMessage()
               ));
        verify(productService, times(0)).updateProductById(anyInt(), any(Product.class));
    }

    @Test
    public void deleteProductById_ok() throws Exception {
        mockMvc.perform(delete("/api/product/{id}", PRODUCT_ID)).andExpect(status().isNoContent());
        verify(productService).deleteProductById(PRODUCT_ID);
    }

    @Test
    public void deleteProductById_noSuchProduct() throws Exception {
        doThrow(new NoSuchProductException(NO_SUCH_PRODUCT_MSG)).when(productService).deleteProductById(eq(PRODUCT_ID));
        mockMvc.perform(delete("/api/product/{id}", PRODUCT_ID))
               .andExpect(status().isNotFound())
               .andExpect(result -> Assertions.assertEquals(
                       NO_SUCH_PRODUCT_MSG,
                       result.getResponse().getErrorMessage()
               ));
        verify(productService).deleteProductById(PRODUCT_ID);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void deleteProductById_wrongPathVariableType() throws Exception {
        mockMvc.perform(delete("/api/product/{id}", "a"))
               .andExpect(status().isBadRequest())
               .andExpect(result -> Assertions.assertEquals(
                       FAIL_TO_CONVERT_MSG,
                       result.getResolvedException().getMessage()
               ));
        verify(productService, times(0)).deleteProductById(anyInt());
    }

    @Test
    public void deleteProductById_negativePathVariable() throws Exception {
        mockMvc.perform(delete("/api/product/{id}", -1))
               .andExpect(status().isBadRequest())
               .andExpect(result -> Assertions.assertEquals(
                       "deleteProductById.id: Id must be positive.",
                       result.getResponse().getErrorMessage()
               ));
        verify(productService, times(0)).deleteProductById(anyInt());
    }

    private void validateAddProduct(String productPath, String expectedResponsePath) throws Exception {
        mockMvc.perform(
                post("/api/product").content(Files.readAllBytes(PRODUCTS_PATH.resolve(productPath)))
                                    .contentType(MediaType.APPLICATION_JSON)
        )
               .andExpect(status().isBadRequest())
               .andExpect(result -> Assertions.assertEquals(
                       objectMapper.readValue(
                               Files.readAllBytes(RESPONSE_PATH.resolve(expectedResponsePath)),
                               ResponseErrorInfo.class
                       ).toString(),
                       objectMapper.readValue(result.getResponse().getContentAsString(), ResponseErrorInfo.class)
                                   .toString()
               ));
        verify(productService, times(0)).addProduct(any());
    }

    private void validateUpdateProduct(String productPath, String expectedResponsePath) throws Exception {
        mockMvc.perform(
                put("/api/product/{id}", PRODUCT_ID).content(Files.readAllBytes(PRODUCTS_PATH.resolve(productPath)))
                                                    .contentType(MediaType.APPLICATION_JSON)
        )
               .andExpect(status().isBadRequest())
               .andExpect(result -> Assertions.assertEquals(
                       objectMapper.readValue(
                               Files.readAllBytes(RESPONSE_PATH.resolve(expectedResponsePath)),
                               ResponseErrorInfo.class
                       ).toString(),
                       objectMapper.readValue(result.getResponse().getContentAsString(), ResponseErrorInfo.class)
                                   .toString()
               ));
        verify(productService, times(0)).updateProductById(anyInt(), any(Product.class));
    }
}
