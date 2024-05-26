package com.example.closetcv3.models

data class FashionStyle(
    val style: String,
    val searchKeywords: String
)

val fashionStyles = listOf(
    FashionStyle(
        style = "Eclectic/Boho",
        searchKeywords = "eclectic fashion, boho fashion"
    ),
    FashionStyle(
        style = "Minimalist",
        searchKeywords = "minimalist fashion, neutral fashion"
    ),
    FashionStyle(
        style = "Soft Girl",
        searchKeywords = "soft girl fashion, pastel fashion"
    ),
    FashionStyle(
        style = "Grunge/Goth",
        searchKeywords = "grunge fashion, goth fashion"
    ),
    FashionStyle(
        style = "Casual/Basic",
        searchKeywords = "casual fashion, basic fashion"
    ),
    FashionStyle(
        style = "Trendy/Streetwear",
        searchKeywords = "trendy fashion, streetwear"
    ),
    FashionStyle(
        style = "Chic/Elegant",
        searchKeywords = "chic fashion, elegant fashion"
    ),
    FashionStyle(
        style = "Athleisure",
        searchKeywords = "athleisure fashion, athletic wear"
    ),
    FashionStyle(
        style = "Cozy/Comfy",
        searchKeywords = "cozy fashion, comfy fashion"
    ),
)
