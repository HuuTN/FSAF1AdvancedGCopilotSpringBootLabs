package com.example.entity;

import com.example.entity.base.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Category> children = new HashSet<>();
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();
    
    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }
    
    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }
    
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }
    
    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }
    
    public boolean isRoot() {
        return parent == null;
    }
    
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }
}
