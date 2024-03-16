package com.example.sportsbooking.Domain.UseCase

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadFashionItemsdetail(imageUri:List<Uri>,context: Context){
    imageUri?.forEach() {uri ->
        val storageRef = FirebaseStorage.getInstance().reference
        val uniqueImageId = UUID.randomUUID()
        val imageRef = storageRef.child("imagesMensFashion/$uniqueImageId")

        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            Toast.makeText(context,"success",Toast.LENGTH_SHORT).show()
        }
        uploadTask.addOnFailureListener{
            Toast.makeText(context,"failure",Toast.LENGTH_SHORT).show()
        }
    }

}