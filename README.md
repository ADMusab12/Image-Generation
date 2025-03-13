## 🌟 Overview
Image Generator is an Android application built with Kotlin that utilizes AI models to generate images based on user prompts. It supports multiple AI models through the **Hugging Face Inference API**, providing different styles of image generation.

## ✨ Features
- 🔹 Generate AI-based images using multiple models
- 🔹 Save generated images to the device gallery
- 🔹 Download and share images easily
- 🔹 Uses **Hugging Face Inference API** for image generation
- 🔹 Optimized for performance with **coroutines and Glide**

## 🛠️ Technologies Used
- **Kotlin** (Android Development)
- **Hugging Face API** (AI Model Inference)
- **Glide** (Image loading & caching)
- **Coroutines** (Efficient background processing)

## 📸 Screenshots

| Flux Model | President Model | Stable Diffusion |
|------------|----------------|------------------|
| ![Flux](https://github.com/user-attachments/assets/88ee0b39-1117-48b7-9373-72feb371da90) | ![President](https://github.com/user-attachments/assets/5c9252c3-3c95-4e8a-bd8f-bf0a34acdc39) | ![Stable Diffusion](https://github.com/user-attachments/assets/a6e5f4c3-af0b-46f6-b702-b276ae283b17) |


## 🚀 Getting Started
### 1️⃣ Clone Repository
```sh
git clone (https://github.com/ADMusab12/Image-Generation.git)
```
### 2️⃣ Setup Hugging Face API Key
1. Go to [Hugging Face](https://huggingface.co/)
2. Create an account and go to **Settings > Access Tokens**
3. Generate a new API token
4. Add the API token in your `gradle.properties`:
   ```properties
   HUGGING_FACE_API_KEY=your_api_key_here
   ```

### 3️⃣ Run the App
- Open the project in **Android Studio**
- Sync the project with Gradle
- Run on an emulator or physical device

## 📡 API Usage
To fetch AI-generated images from Hugging Face models, we use the **Inference API**:
```kotlin
suspend fun fetchImage(prompt: String): Bitmap? {
    val response = khttp.post(
        url = "https://api-inference.huggingface.co/models/model_name",
        headers = mapOf("Authorization" to "Bearer $HUGGING_FACE_API_KEY"),
        json = mapOf("inputs" to prompt)
    )
    return response.jsonObject.get("image") as Bitmap
}
```

⭐ Don't forget to star the repo if you like it! 🚀

