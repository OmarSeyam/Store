package com.example.project.model

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId
    var id:String,
    var name: String?,
    var img: String?,
    var detail: String?,
    var category: String?,
    var price: Double?,
    var numOfCopySold: Any?,
    var numOfCopies: Any?,
    var sumRate: Float?,
    var numRate: Int?
)