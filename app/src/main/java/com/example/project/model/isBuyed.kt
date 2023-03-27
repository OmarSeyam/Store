package com.example.project.model

import com.google.firebase.firestore.DocumentId

data class isBuyed(@DocumentId var id:String, var name:String,var img:String
,var customer_id:String, var product_id:String, var price:Double, var numOfCopy:Int)