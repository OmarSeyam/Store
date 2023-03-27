package com.example.project.model

import com.google.firebase.firestore.DocumentId

data class Category(@DocumentId var id:String, var name:String)