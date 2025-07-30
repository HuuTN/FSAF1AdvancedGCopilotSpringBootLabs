1. In Task 1 (Refactoring placeOrder), what specific "code smell" did the original method have? How does the "Extract Method" refactoring improve the code's maintainability ? 
    Code Smells Identified :
    
        1. Long Method Smell 🚨
        The original method was doing too many things in a single function - validation, entity creation, stock management, item processing, and persistence.

        2. Single Responsibility Principle (SRP) Violation 🚨
        The method had multiple reasons to change:

        Changes in validation logic
        Changes in order creation logic
        Changes in stock management logic
        Changes in item processing logic
        Changes in persistence logic
        
        3. Low Cohesion 🚨
        Different parts of the method were dealing with unrelated concerns, making it hard to understand the overall flow.

        4. High Cyclomatic Complexity 🚨
        Multiple nested loops and conditional statements made the method difficult to test and debug.

        5. Poor Readability 🚨
        The business logic was buried in implementation details, making it hard to understand the high-level workflow

2. In Task 2 (Debugging), which of the three bugs was the most difficult for you to create a prompt for? Why do you think that is? What makes a good prompt for debugging?
    2.1 Race Condition Bug in Stock Management 🚨 (Most Difficult):
       Reason : 
        + Intermittent Nature: Only occurs under concurrent load
        + Environment Dependent: Hard to reproduce in development
        + Complex Root Cause: Requires understanding of database transactions and concurrency
        + Misleading Symptoms: Appears as business logic errors (negative stock, overselling)
    2.2. Null Validation Logic Error
        Reason : 
        + NullPointerException Risk: If getProductId() or getQuantity() returns null, calling .       toString() will throw NPE
        + Logic Flaw: Converting numbers to strings for null checking is unnecessary and error-prone
        + Misleading Error Messages: May throw NPE instead of the intended validation message
    2.3. Missing Rollback Logic for Failed Order Items
        Reason :
        + Partial State Corruption: Some order items succeed while others fail
        + Data Consistency Issues: Stock levels become inconsistent
        + Complex Recovery: Need to understand transaction boundaries and rollback mechanisms

3. In Task 3 (Security Analysis), besides the issues Copilot found, can you think of one other potential security risk in a typical e-commerce application?
    I can base on the "OrderServiceImpl" , I can list these :
        * The Vulnerability:
            In my current OrderServiceImpl.placeOrder method, there's a significant security flaw:
            - Attack Scenario : 
                + Malicious User finds a way to modify the product price in the database (through SQL injection, compromised admin account, or timing attacks)
                + Race Condition Attack: User places order → Admin/Attacker changes price to $0.01 → Order processes with manipulated price
                + Frontend Manipulation: If price validation only happens on frontend, attacker can bypass it entirel
            - Real-World Impact
                + Financial Loss: Products sold at $0.01 instead of $999.99
                + Inventory Fraud: Bulk orders at manipulated prices
                + Business Disruption: Massive financial losses before detection