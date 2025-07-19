# KMP E-Commerce Store

A fully featured, cross-platform food supplements store built from the ground up using Kotlin Multiplatform and Jetpack Compose.

## 🚀 Key Features

- **Multi-Platform UI**  
  Share a single Compose UI layer across Android & iOS with Compose Multiplatform.  
- **User Authentication**  
  Secure Google Sign-In via Firebase Auth.  
- **Product Catalog & Shopping Cart**  
  Browse multiple categories, customize products (e.g. flavors), add to cart, update quantities.  
- **Checkout & Payments**  
  - **Pay on Delivery**  
  - **PayPal Integration** via PayPal API  
- **Order Confirmation Emails**  
  Automated order emails (product details, totals, shipping info) powered by Firebase Cloud Functions.  
- **Admin Panel**  
  Secure CRUD interface for store owners to add, update, or remove products.  
- **Push Notifications**  
  Notify customers of order updates using Firebase Cloud Messaging.

## 🔧 Architecture & Tech Stack

- **Kotlin Multiplatform (KMP)**  
  Shared business logic across platforms  
- **Jetpack Compose Multiplatform**  
  Declarative UI: From Figma mockups to native app screens  
- **Clean, Multi-Modular MVVM**  
  Scalable, testable modules & clear separation of concerns  
- **Firebase Backend**  
  - **Authentication** (Google Sign-In)  
  - **Cloud Firestore** (real-time database + security rules)  
  - **Storage** (product images)  
  - **Cloud Functions** (email automation)  
  - **Cloud Messaging** (push notifications)  

## 🗂️ Project Scope

This repository contains the complete source for the e-commerce app built in the course:
- End-to-end Android & iOS implementation using shared Kotlin UI  
- Real-world features: authentication, cart, payments, emails, notifications  
- Admin dashboard for live product management  
- Comprehensive Firebase integration for backend services 
