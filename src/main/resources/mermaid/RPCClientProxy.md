```mermaid
graph TB
    A[Start] --> B[Create RPCClientProxy instance]
    B --> C[Invoke method on proxy]
    C --> D{Check Service annotation}
    D -->|Exists| E[Get URL from annotation]
    D -->|Does not exist| F[Find URL from targetService methods]
    E --> G[Create RPCRequest]
    F --> G
    G --> H[Send RPCRequest using RestClient]
    H --> I[Get RPCResponse]
    I --> J[Return data from response]
    J --> K[End]