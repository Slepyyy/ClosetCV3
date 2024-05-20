package com.example.closetcv3.models

data class FashionStyle(
    val style: String,
    val description: String,
    val searchKeywords: String
)

val fashionStyles = listOf(
    FashionStyle(
        style = "Eclectic/Boho",
        description = "Bright, Bold Colors",
        searchKeywords = "eclectic fashion, boho fashion"
    ),
    FashionStyle(
        style = "Minimalist",
        description = "Neutrals",
        searchKeywords = "minimalist fashion, neutral fashion"
    ),
    FashionStyle(
        style = "Soft Girl",
        description = "Pastels",
        searchKeywords = "soft girl fashion, pastel fashion"
    ),
    FashionStyle(
        style = "Grunge/Goth",
        description = "Dark Tones",
        searchKeywords = "grunge fashion, goth fashion"
    ),
    FashionStyle(
        style = "Casual/Basic",
        description = "Jeans and a Comfy T-shirt",
        searchKeywords = "casual fashion, basic fashion"
    ),
    FashionStyle(
        style = "Trendy/Streetwear",
        description = "Modern Stylish",
        searchKeywords = "trendy fashion, streetwear"
    ),
    FashionStyle(
        style = "Chic/Elegant",
        description = "Fancy",
        searchKeywords = "chic fashion, elegant fashion"
    ),
    FashionStyle(
        style = "Athleisure",
        description = "Athletic Wear",
        searchKeywords = "athleisure fashion, athletic wear"
    ),
    FashionStyle(
        style = "Cozy/Comfy",
        description = "Oversized",
        searchKeywords = "cozy fashion, comfy fashion"
    )
)
