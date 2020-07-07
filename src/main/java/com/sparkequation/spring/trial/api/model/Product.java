package com.sparkequation.spring.trial.api.model;

import com.sparkequation.spring.trial.api.validator.ProductExpirationDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "PRODUCT")
public class Product {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "PRODUCT_SEQ")
    @SequenceGenerator(name = "PRODUCT_SEQ", sequenceName = "PRODUCT_SEQ", allocationSize = 1, initialValue = 30)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Size(min = 1, max = 255, message = "Product name must have length from 1 to 255.")
    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "ISFEATURED")
    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = rating > 8 || featured;
    }

    @ProductExpirationDate(message = "Expiration date must expire not less than 30 days since now.")
    @Basic
    @Column(name = "EXPIRATIONDATE")
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Basic
    @Column(name = "ITEMSINSTOCK")
    public int getItemsInStock() {
        return itemsInStock;
    }

    public void setItemsInStock(int itemsInStock) {
        this.itemsInStock = itemsInStock;
    }

    @Basic
    @Column(name = "RECEIPTDATE")
    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    @Basic
    @Column(name = "RATING")
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @NotNull(message = "A product must have brand.")
    @ManyToOne(targetEntity = Brand.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "BRANDID", foreignKey = @ForeignKey(name = "fk_brand"))
    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @NotNull(message = "A product must have categories.")
    @Size(min = 1, max = 5, message = "A product must have from 1 to 5 categories.")
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "CATEGORY_PRODUCT",
            joinColumns = {
                    @JoinColumn(name = "PRODUCTID", referencedColumnName = "ID",
                            foreignKey = @ForeignKey(name = "fk_r_product_category"))},
            inverseJoinColumns = {
                    @JoinColumn(name = "CATEGORYID", referencedColumnName = "ID",
                            foreignKey = @ForeignKey(name = "fk_r_category_product"))
            })
    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
               featured == product.featured &&
               itemsInStock == product.itemsInStock &&
               Double.compare(product.rating, rating) == 0 &&
               Objects.equals(name, product.name) &&
               Objects.equals(expirationDate, product.expirationDate) &&
               Objects.equals(receiptDate, product.receiptDate) &&
               Objects.equals(brand, product.brand) &&
               Objects.equals(categories, product.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, featured, expirationDate, itemsInStock, receiptDate, rating, brand, categories);
    }

    public void update(Product product) {
        name = product.name;
        expirationDate = product.expirationDate;
        itemsInStock = product.itemsInStock;
        receiptDate = product.receiptDate;
        rating = product.rating;
        featured = rating > 8 || product.featured;
        brand = product.brand;
        categories = product.categories;
    }

    private int id;
    private String name;
    private boolean featured;
    private Date expirationDate;
    private int itemsInStock;
    private Date receiptDate;
    private double rating;

    private Brand brand;
    private Set<Category> categories;
}
