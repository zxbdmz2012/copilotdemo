```mermaid
graph TB
    A[Start] --> B[Get all bean definition names]
    B --> C[Iterate over each bean name]
    C --> D[Get bean instance]
    D --> E[Get class of bean instance]
    E --> F{Check if class has RegisterService annotation}
    F -- Yes --> G[Add bean to targetService provider]
    G --> H[Next bean]
    F -- No --> I[Get all methods of bean class]
    I --> J[Iterate over each method]
    J --> K{Check if method has RegisterService annotation}
    K -- Yes --> L[Add bean to targetService provider and break loop]
    L --> H
    K -- No --> M[Next method]
    M --> J
    H --> C
    C --> N[End]