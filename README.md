## ProductX  (Swipe Assignment)

ProductX is a Jetpack Compose-based Android application that displays a list of products and allows users to add new products seamlessly.  

### Features  
- **Product Listing**:  
  - View a list of products fetched from an API.  
  - Search for specific products.  
  - Navigate to the "Add Product" screen.  
  - Display images from URLs (with a default fallback).  

- **Add Product**:  
  - Enter product details (name, type, price, tax).  
  - Select product type from a predefined list.  
  - Upload images in JPEG/PNG format (1:1 ratio).  
  - Submit product details to the server.  
  - Offline mode: Saves created products locally and syncs when online.  

### Tech Stack  
- **Jetpack Compose** – UI development  
- **MVVM Architecture** – Code organization  
- **Retrofit** – API integration  
- **Koin** – Dependency Injection  
- **Coroutines & Lifecycle** – Background tasks  

### API Used  
- **Fetch Products** (GET):  
  `https://app.getswipe.in/api/public/get`  
- **Add Product** (POST):  
  `https://app.getswipe.in/api/public/add`  

### Screenshots  
| Product List Screen | Add Product Screen |  
|--------------------|-------------------|  
| ![Product List](https://github.com/prafullKrRj/ProductX/blob/master/ss/main.jpg?raw=true) | ![Add Product](https://github.com/prafullKrRj/ProductX/blob/master/ss/add.jpg?raw=true) |  
