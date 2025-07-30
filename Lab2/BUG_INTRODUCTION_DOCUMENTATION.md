# 🐛 Bug Introduction Documentation

## Tổng quan
Đã cẩn thận giới thiệu **3 bugs cụ thể** vào code Spring Boot đã hoạt động để mô phỏng các lỗi phổ biến trong thực tế phát triển phần mềm.

---

## 🐛 **Bug A - Logic Error**

### **Vị trí**: `OrderServiceImpl.createOrderItem()`
### **Mô tả lỗi**: 
- **Trước**: `orderItem.setPrice(product.getPrice());` (logic đúng)
- **Sau**: `orderItem.setPrice(9999.99);` (hardcoded wrong value)

### **Code bị thay đổi**:
```java
private OrderItem createOrderItem(Order order, Product product, CreateOrderRequestDTO.OrderItemRequest itemReq) {
    OrderItem orderItem = new OrderItem();
    orderItem.setOrder(order);
    orderItem.setProduct(product);
    orderItem.setQuantity(itemReq.getQuantity());
    // BUG A (Logic Error): Hardcoded wrong price instead of using product.getPrice()
    orderItem.setPrice(9999.99);
    return orderItem;
}
```

### **Ảnh hưởng**:
- ✅ **Compilation**: Vẫn compile thành công
- ❌ **Business Logic**: Tất cả orders sẽ có giá `9999.99` bất kể sản phẩm thực tế
- ❌ **Revenue**: Gây sai lệch doanh thu nghiêm trọng
- ❌ **Customer Experience**: Khách hàng sẽ bị charge sai giá
- ❌ **Data Integrity**: Dữ liệu về giá không còn chính xác

### **Phát hiện**:
- Unit tests checking order total calculations sẽ fail
- Integration tests với real scenarios sẽ fail
- Manual testing sẽ thấy giá sai ngay lập tức

---

## 🐛 **Bug B - Runtime Exception**

### **Vị trí**: `ProductServiceImpl.getById()` và `findProductById()`
### **Mô tả lỗi**:
- **Trước**: `return productRepository.findById(id).map(this::toDTO);` (proper Optional handling)
- **Sau**: `return null;` (null instead of Optional/exception)

### **Code bị thay đổi**:
```java
@Override
public Optional<ProductDTO> getById(Long id) {
    // BUG B (Runtime Exception): Return null instead of proper Optional handling
    // This will cause NullPointerException downstream when code expects Optional
    return null;
}

public Product findProductById(Long id) {
    // BUG B (Runtime Exception): Return null instead of throwing exception
    // This will cause NullPointerException when downstream code tries to use the product
    return null;
}
```

### **Ảnh hưởng**:
- ✅ **Compilation**: Vẫn compile thành công
- ❌ **Runtime**: `NullPointerException` khi code gọi `.map()`, `.orElse()`, hoặc access properties
- ❌ **API Responses**: 500 Internal Server Error thay vì 404 Not Found
- ❌ **Error Handling**: Không có proper error messages cho client
- ❌ **User Experience**: Crash thay vì graceful error handling

### **Scenarios gây lỗi**:
- `productService.getById(999L).map(...)` → NPE
- `Product p = findProductById(999L); p.getName()` → NPE
- Any code expecting Optional behavior sẽ crash

---

## 🐛 **Bug C - Data Integrity**

### **Vị trí**: `OrderServiceImpl.placeOrder()`
### **Mô tả lỗi**:
- **Trước**: `@Transactional` (atomic operations)
- **Sau**: `// @Transactional` (commented out - no transaction management)

### **Code bị thay đổi**:
```java
@Override
// BUG C (Data Integrity): Commented out @Transactional annotation
// This removes atomicity - if order save succeeds but order items fail,
// we'll have inconsistent data with orphaned orders
// @Transactional
public OrderDTO placeOrder(CreateOrderRequestDTO request) {
    // ... existing logic
}
```

### **Ảnh hưởng**:
- ✅ **Compilation**: Vẫn compile thành công
- ✅ **Happy Path**: Hoạt động bình thường khi không có lỗi
- ❌ **Error Scenarios**: Mất tính atomicity
- ❌ **Data Consistency**: Có thể tạo ra:
  - Orders without OrderItems
  - Stock đã bị trừ nhưng Order fail
  - Partial data commits
- ❌ **Rollback**: Không tự động rollback khi có lỗi

### **Scenarios gây lỗi**:
1. **Order save thành công** → **OrderItem save fail** → Orphaned order
2. **Stock update thành công** → **Order save fail** → Stock inconsistent
3. **Database connection issue** giữa operations → Partial commit

---

## 🧪 **Testing và Verification**

### **Bug A Testing**:
```java
@Test
void testOrderPricing() {
    // This test WILL FAIL due to Bug A
    Product product = createProduct("Test", 50.0);
    Order order = placeOrder(product, 2);
    assertEquals(100.0, order.getTotalPrice()); // Expected: 100, Actual: 19999.98
}
```

### **Bug B Testing**:
```java
@Test
void testProductNotFound() {
    // This test WILL THROW NPE due to Bug B
    Optional<ProductDTO> result = productService.getById(999L);
    assertTrue(result.isEmpty()); // NPE here because result is null
}
```

### **Bug C Testing**:
```java
@Test
@Transactional
@Rollback
void testOrderTransactionRollback() {
    // This test WILL FAIL due to Bug C
    // Database will have inconsistent state when exception occurs
    // during order placement
}
```

---

## 🔍 **Bug Classification**

| Bug | Type | Severity | Detection Time | Impact |
|-----|------|----------|---------------|---------|
| **A** | Logic Error | HIGH | Immediate (first test) | Business Critical |
| **B** | Runtime Exception | MEDIUM | Runtime | Application Crash |
| **C** | Data Integrity | HIGH | Stress/Error scenarios | Data Corruption |

---

## 🚨 **Production Impact**

### **Bug A (Logic Error)**:
- **Financial Loss**: Customers charged wrong amounts
- **Business Impact**: Revenue calculations completely wrong
- **Legal Issues**: Potential refund demands

### **Bug B (Runtime Exception)**:
- **User Experience**: 500 errors instead of 404
- **System Stability**: Frequent crashes
- **Monitoring**: High error rates in logs

### **Bug C (Data Integrity)**:
- **Data Quality**: Inconsistent database state
- **Audit Issues**: Data doesn't match business rules
- **Recovery Complexity**: Manual data cleanup required

---

## 📋 **Next Steps**

Các bugs này đã được introduce để:
1. **Testing Practice**: Verify test suites can catch these issues
2. **Debugging Skills**: Practice identifying and fixing common bugs
3. **Code Review**: Understand what to look for in reviews
4. **Monitoring**: Set up alerts for these types of issues

**⚠️ WARNING**: Các bugs này chỉ dành cho mục đích training. Trong production, cần revert lại code working ban đầu!
