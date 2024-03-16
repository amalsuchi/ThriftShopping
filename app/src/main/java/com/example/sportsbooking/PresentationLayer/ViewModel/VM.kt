package com.example.sportsbooking.PresentationLayer.ViewModel

import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDecay
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsbooking.Domain.location.LocationTracker
import com.example.sportsbooking.PresentationLayer.DataClass.FashionProductDetailData
import com.example.sportsbooking.PresentationLayer.DataClass.ProductDataFlow
import com.example.sportsbooking.PresentationLayer.DataClass.Response
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VM @Inject constructor(private val locationTracker: LocationTracker) :ViewModel(){


    val auth = Firebase.auth
    val uid = auth.currentUser?.uid  ?: "defaultUid"
    val mail = auth.currentUser?.email  ?: "defaultEmail"
    val db = Firebase.firestore

    private val _uiState = mutableStateOf(Response())
    val uiState : State<Response> = _uiState

    private val _products = MutableStateFlow<List<ProductDataFlow>>(emptyList())
    val products: StateFlow<List<ProductDataFlow>> = _products

    private val _errorState = mutableStateOf<String?>(null)
    val errorState :State<String?> = _errorState

    fun fetchProducts() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid  ?: "defaultUid"
            val db = FirebaseFirestore.getInstance()
            db.collection("NormalUsers").document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val location = documentSnapshot.getString("normallocation")
                    if (location != null) {
                        val wishlistedProducts = documentSnapshot.get("Wishlist") as List<String>

                        fetchProductsBasedOnLocation(location,wishlistedProducts)

                    } else {
                        Log.d("listdata","error")
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }



    fun fetchProductsBasedOnLocation(location: String,wishlistedProducts:List<String>){
        val db = FirebaseFirestore.getInstance()
        viewModelScope.launch {
            db.collection("Products").whereEqualTo("Location", location)
                .get()
                .addOnSuccessListener {querySnapshot->
                    val productsList = querySnapshot.documents.mapNotNull { documentSnapshot ->
                        val product = documentSnapshot.toObject(ProductDataFlow::class.java)
                        if(!wishlistedProducts.contains(product?.id)){
                            product
                        }else{
                            null
                        }
                    }
                    _products.value = productsList
                }
                .addOnFailureListener {

                }
        }
    }

    fun addItemToLikedProducts(itemToAdd:String){
        val uid = auth.currentUser?.uid  ?: "defaultUid"
        val firestore = FirebaseFirestore.getInstance()
        val documentRef = firestore.collection("NormalUsers").document(uid)

        documentRef.update("Wishlist",FieldValue.arrayUnion(itemToAdd))
            .addOnSuccessListener {
                Log.d("wishlist","sucess")
            }.addOnFailureListener {
                Log.d("wishlist","failure")
            }

    }


    fun AddOrUpdateAddress(name: String,email:String,nlocation:String,exactlocation:String,context:Context,onComplete:()->Unit){
        val docref =db.collection("NormalUsers").document(uid)
        docref.get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val docExists = task.result.exists()
                if(docExists){
                    val user = hashMapOf<String,Any>(

                        "normallocation" to nlocation,
                        "exactlocation" to exactlocation
                    )
                    docref.update(user)
                        .addOnSuccessListener {
                            _uiState.value =uiState.value.copy(
                                isLoading = false,
                                location = nlocation,
                                isSuccess = true,
                                errorMessage = null
                            )
                            onComplete()
                        }
                        .addOnFailureListener { e->
                            _uiState.value = uiState.value.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = e.localizedMessage
                            )
                            _errorState.value = e.localizedMessage
                        }
                }else{
                    _uiState.value =uiState.value.copy(isLoading = true)

                    val user = hashMapOf<String,Any>(
                        "name" to name,
                        "email" to email,
                        "normallocation" to nlocation,
                        "exactlocation" to exactlocation,
                        "wishlist" to emptyList<String>()

                    )

                    db.collection("NormalUsers").document(uid).set(user)
                        .addOnSuccessListener {
                            _uiState.value =uiState.value.copy(
                                isLoading = false,
                                location = nlocation,
                                isSuccess = true,
                                errorMessage = null
                            )
                        }
                        .addOnFailureListener { e->
                            _uiState.value = uiState.value.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = e.localizedMessage
                            )
                            _errorState.value = e.localizedMessage
                        }

                }
            }else{
                Toast.makeText(context,"Sorry an error occured",Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun UpdateUserData(name: String,email:String){
        _uiState.value =uiState.value.copy(isLoading = true)

        val user = hashMapOf<String,Any>(
            "name" to name,
            "email" to email,

        )

        db.collection("NormalUsers").document("${uid}").update(user)
            .addOnSuccessListener {
                _uiState.value =uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
            }
            .addOnFailureListener { e->
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.localizedMessage
                )
                _errorState.value = e.localizedMessage
            }
    }

    suspend fun uploadFashionItemsdetail(
        For:String,
        username:String,
        itemname:String,
        location:String,
        Size:String,
        Detail:String,
        Price:Int,
        imageUri:List<Uri>){
        _uiState.value =uiState.value.copy(isLoading = true)

        var urlToFirestore = mutableListOf<String>()
        val storageRef = FirebaseStorage.getInstance().reference
        val uniqueImageId = UUID.randomUUID().toString()
        val currentTimeinMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val TimeStamp = sdf.format(Date(currentTimeinMillis))

        imageUri.forEachIndexed { index, uri ->
            val uniqueImageId = UUID.randomUUID().toString() // Ensure this is unique for each image
            val imageRef = storageRef.child(uniqueImageId)
            val uploadTask = imageRef.putFile(uri)
            val snapshot = uploadTask.await()
            val downloadUrl = snapshot.metadata?.reference?.downloadUrl?.await()?.toString()
            downloadUrl?.let { urlToFirestore.add(it) }
            Log.d("Upload", "Uploaded image $index with ID: $uniqueImageId")
        }
        val Productdetails = hashMapOf<String,Any>(
            "For" to For,
            "name" to itemname,
            "Size" to Size,
            "Detail" to Detail,
            "Price" to Price,
            "Uid" to uid,
            "UserName" to username,
            "Email" to mail,
            "Location" to location,
            "imageUri" to urlToFirestore,
            "TimeStamp" to TimeStamp,
            "id" to uniqueImageId

        )
        try {
            db.collection("Products").document(uniqueImageId).set(Productdetails).await()
            _uiState.value =uiState.value.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null
            )
        } catch (e: Exception) {
            _uiState.value = uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = e.localizedMessage
            )
            _errorState.value = e.localizedMessage // Ensure _errorState is defined and accessible
        }
    }

    suspend fun ShowUserAdds(){
        val uid = auth.currentUser?.uid  ?: "defaultUid"
        _uiState.value =uiState.value.copy(isLoading = true)
        //var documents = mutableListOf<DocumentSnapshot>()
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("Products").whereEqualTo("Uid",uid).get()

         try {
            val result = query.await()
            _uiState.value =uiState.value.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null,
                userAdds = result.documents
            )
             Log.d("Success", "Error getting documents: ")

        }catch (e:Exception){
            _uiState.value = uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = e.localizedMessage
            )
             Log.e("FailedViewModel", "Error getting documents: $e")

        }

    }

    suspend fun showAllProductsInHome(Location:String){
        //val uid = auth.currentUser?.uid ?: "empty"
        val db = FirebaseFirestore.getInstance()
        _uiState.value =uiState.value.copy(isLoading = true)


        val query = db.collection("Products").whereEqualTo("Location",Location).get()

        try {
            val result = query.await()
            _uiState.value = uiState.value.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null,
                userAdds = result.documents
            )
        }
        catch (e:Exception) {
            _uiState.value = uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = e.localizedMessage
            )
            Log.e("FailedViewModel", "Error getting documents: $e")
        }

    }

    fun ShowDetailedUserAdds(id:String){

        _uiState.value =uiState.value.copy(isLoading = true)
        val db = FirebaseFirestore.getInstance()
        db.collection("Products").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.toObject(FashionProductDetailData::class.java)
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null,
                        FashionUserProductData = data
                        )
                    Log.d("Success", "Error getting documents: ")
                }else {

                }
            }.addOnFailureListener { e->
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.localizedMessage
                )
                _errorState.value = e.localizedMessage
            }


    }

    suspend fun updateFashionItemsdetail(
        id: String,
        itemname: String,
        location: String,
        Size: String,
        Detail: String,
        Price: Int,
         // Make this parameter optional
    ) {
        _uiState.value = uiState.value.copy(isLoading = true)

        val storageRef = FirebaseStorage.getInstance().reference
        val currentTimeinMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val TimeStamp = sdf.format(Date(currentTimeinMillis))

        // Check if imageUri is not null before iterating over it

        val Productdetails = hashMapOf<String, Any>(
            "name" to itemname,
            "Size" to Size,
            "Detail" to Detail,
            "Price" to Price,
            "Uid" to uid,
            "Email" to mail,
            "Location" to location,
            "TimeStamp" to TimeStamp,
        )
        try {
            db.collection("Products").document(id).update(Productdetails).await()
            _uiState.value = uiState.value.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null
            )
        } catch (e: Exception) {
            _uiState.value = uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = e.localizedMessage
            )
            _errorState.value = e.localizedMessage // Ensure _errorState is defined and accessible
        }
    }

    suspend fun uploadImages(id:String,imageUri:List<Uri>){

        val downloadurl = mutableListOf<String>()
        val uniqueImageId = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child(uniqueImageId)
        imageUri.forEach() { uri ->
            val uploadTask = imageRef.putFile(uri)
            val snapshot = uploadTask.await()
            val downloadurlss = snapshot.storage.downloadUrl.await()
            downloadurl.add(downloadurlss.toString())
        }

        try {
            db.collection("Products").document(id).update("imageUri", FieldValue.arrayUnion(*downloadurl.toTypedArray())).await()
            _uiState.value = uiState.value.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null
            )
        }
        catch (e: Exception) {
            _uiState.value = uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = e.localizedMessage
            )
            _errorState.value = e.localizedMessage // Ensure _errorState is defined and accessible
        }
    }

    fun deleteImage(imagesToDelete: List<String>,id:String) {
        viewModelScope.launch {
            try {
                _uiState.value = uiState.value.copy(
                    isLoading = true,

                )
                val firestore = FirebaseFirestore.getInstance()
                val storageRef = FirebaseStorage.getInstance().reference

                val collection = firestore.collection("Products").document(id)
                val documents = collection.get().await()

                if (documents != null) {
                    val images = documents.get("imageUri") as List<String>
                    val updatedImages = images.filter { it !in imagesToDelete }
                    collection.update("imageUri", updatedImages).await()


                    imagesToDelete.forEach { name ->
                        val imageRef = storageRef.child(name)
                        imageRef.delete().await()
                    }
                }

                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null,

                )
            } catch (e: Exception) {
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.localizedMessage
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getUserData() {
        _uiState.value = uiState.value.copy(isLoading = true)

        db.collection("NormalUsers").document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data =
                        documentSnapshot.toObject(com.example.sportsbooking.PresentationLayer.DataClass.UserData::class.java)
                    // Handle the retrieved user data here
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null,
                        user = data
                    )
                } else {
                    // Document does not exist
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "User data does not exist."
                    )
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = exception.localizedMessage
                )
                _errorState.value = exception.localizedMessage
            }
    }







}