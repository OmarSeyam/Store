package com.example.project.model

import com.google.firebase.firestore.DocumentId

data class Customer(@DocumentId var id:String, var name:String, var email:String, var password:String)