package com.example.project.model

import com.google.firebase.firestore.DocumentId

data class isRated(@DocumentId var id:String, var customer_id:String, var product_id:String, var rate:Float)