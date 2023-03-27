package com.example.project.model

import com.google.firebase.firestore.DocumentId

data class Admin(@DocumentId var id:String, var name:String, var phone:String, var email:String, var password:String)