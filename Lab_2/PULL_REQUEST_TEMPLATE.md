1. In Task 1 (Refactoring placeOrder), what specific "code smell" did the original method have? How does the "Extract Method" refactoring improve the code's maintainability?

Answer :

    1. Long Method Code Smell
        The original placeOrder method was handling multiple responsibilities in a single, lengthy method:

        Order entity creation and initialization
        Product validation and retrieval
        Stock availability checking
        Inventory management (stock deduction)
        OrderItem creation and persistence
        Order assembly and final persistence
    2. Single Responsibility Principle (SRP) Violation
        The method was doing "too many things" - it violated the principle that a method should have only one reason to change. The original method would need to be modified for changes in:

        Order creation logic
        Product validation rules
        Stock management policies
        OrderItem creation process
        Persistence strategies
    3. Low Cohesion
        Different parts of the method were performing unrelated operations, making the code harder to understand and follow.

    4. Poor Abstraction Level
        The method mixed high-level orchestration logic with low-level implementation details, making it difficult to understand the overall business flow.

2. In Task 2 (Debugging), which of the three bugs was the most difficult for you to create a prompt for? Why do you think that is? What makes a good prompt for debugging?
    Answer :

    1. Ambiguous Implementation Requirements
        Your prompt said: "make the findProductById method return null if a product is not found"

        The challenge was that no findProductById method existed in the current codebase. I had to:

        Create the method signature in the ProductService interface
        Implement it in ProductServiceImpl
        Decide how to structure it to cause the intended NullPointerException
    
    2. Missing Context About Usage
        The prompt didn't specify:

        Where this method would be called from
        How the downstream NullPointerException should manifest
        Whether to modify existing code to use this buggy method
    
    3. Architectural Decisions Required
        I had to make several assumptions:

        Should I replace existing Optional<Product> patterns?
        How to integrate this with the current repository layer?
        What the method signature should look like


3. In Task 3 (Security Analysis), besides the issues Copilot found, can you think of one other potential security risk in a typical e-commerce application?

    1. Authorization and Access Control Vulnerability
        The Risk:
        The current OrderService interface lacks proper authorization checks to ensure users can only access and manipulate their own orders. This could lead to Insecure Direct Object Reference (IDOR) attacks.

    2. Attack Scenarios:
        Order Enumeration Attack:

        Attacker can call getOrderById(1), getOrderById(2), etc.
        Access other customers' order details, addresses, payment info
        Unauthorized Order Modification:

        User could modify orders that don't belong to them
        Change delivery addresses, cancel other users' orders
        Data Exposure:

        getDashboardStats() might expose sensitive business metrics
        No role checking for admin-only functionality