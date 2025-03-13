package com.codetech.texttoimage.abstraction.model

data class HuggingFaceRequest(
    val inputs: String,
    val image: ByteArray? = null
)

