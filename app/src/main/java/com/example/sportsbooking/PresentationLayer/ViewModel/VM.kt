package com.example.sportsbooking.PresentationLayer.ViewModel

import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsbooking.Data.location.LocationHelper
import com.example.sportsbooking.PresentationLayer.DataClass.FashionProductDetailData
import com.example.sportsbooking.PresentationLayer.DataClass.ProductDataFlow
import com.example.sportsbooking.PresentationLayer.DataClass.Response
import com.example.sportsbooking.PresentationLayer.DataClass.UiResponse
import com.example.sportsbooking.PresentationLayer.DataClass.UserInfoData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VM @Inject constructor( private val locationHelper: LocationHelper) :ViewModel(){


    val auth = Firebase.auth
    val uid = auth.currentUser?.uid  ?: "defaultUid"
    val mail = auth.currentUser?.email  ?: "defaultEmail"
    val db = Firebase.firestore

    private val _jwtToken = MutableStateFlow<String?>(null)
    val jwtToken = _jwtToken.asStateFlow()

    fun getJwtToken(){
        viewModelScope.launch {
            val currentUser = auth.currentUser
            currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val token = task.result?.token
                    _jwtToken.value =token
                }
            }
        }
    }

    private val _uiState = mutableStateOf(Response())
    val uiState : State<Response> = _uiState

    private val _products = MutableStateFlow<List<ProductDataFlow>>(emptyList())
    val products: StateFlow<List<ProductDataFlow>> = _products

    private val _uiResponse = MutableStateFlow(UiResponse())
    val uiResponse: StateFlow<UiResponse> = _uiResponse

    private val _errorState = mutableStateOf<String?>(null)
    val errorState :State<String?> = _errorState


    val isLocationEnabled = MutableStateFlow(false)

    init {
        updateLocationServiceStatus()
    }
     fun updateLocationServiceStatus() {
        isLocationEnabled.value = locationHelper.isConnected()
    }

    fun enableLocationRequest(
        context: Context,
        makeRequest: (intentSenderRequest: IntentSenderRequest) -> Unit//Lambda to call when locations are off.
    ) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())//Checksettings with building a request
        task.addOnSuccessListener { locationSettingsResponse ->
            Log.d(
                "Location",
                "enableLocationRequest: LocationService Already Enabled"
            )
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()//Create the request prompt
                    makeRequest(intentSenderRequest)//Make the request from UI
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    fun createOrUpdateDocument(name: String, email:String, onComplete: () -> Unit){
        val docref =db.collection("NormalUsers").document(uid)
        docref.get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val docExists = task.result.exists()
                if(docExists){
                    val user = hashMapOf<String,Any>(
                        "name" to name,
                        "email" to email,
                        "uid" to uid
                    )
                    docref.update(user)
                        .addOnSuccessListener {
                            _uiResponse.value =uiResponse.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                            onComplete()
                        }
                        .addOnFailureListener { e->
                            _uiResponse.value =uiResponse.value.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = e.localizedMessage
                            )
                            _errorState.value = e.localizedMessage
                        }
                }else {
                    _uiResponse.value = uiResponse.value.copy(isLoading = true)

                    val user = hashMapOf<String, Any>(
                        "name" to name,
                        "email" to email,
                        "uid" to uid.toString(),
                        "latitude" to 0,
                        "longitude" to 0,
                        "approxGeolocation" to "",
                        "exactGeolocation" to "",
                        "wishlist" to emptyList<String>()

                    )

                    db.collection("NormalUsers").document(uid).set(user)
                        .addOnSuccessListener {
                            _uiResponse.value = uiResponse.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                        }
                        .addOnFailureListener { e ->
                            _uiResponse.value = uiResponse.value.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = e.localizedMessage
                            )
                            _errorState.value = e.localizedMessage
                        }

                }
            }
        }
    }

    fun updateLocation(latitude: Double,longitude:Double, approxGeolocation:String,exactGeolocation:String){
        val docref =db.collection("NormalUsers").document(uid)
        docref.get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val docExists = task.result.exists()
                if(docExists){
                    val user = hashMapOf<String,Any>(
                        "latitude" to latitude,
                        "longitude" to longitude,
                        "approxGeolocation" to approxGeolocation,
                        "exactGeolocation" to exactGeolocation
                    )
                    docref.update(user)
                        .addOnSuccessListener {
                            _uiResponse.value =uiResponse.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                        }
                        .addOnFailureListener { e->
                            _uiResponse.value =uiResponse.value.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = e.localizedMessage
                            )
                            _errorState.value = e.localizedMessage
                        }
                }
            }
        }
    }

    fun fetchUserData(){
        viewModelScope.launch {
            val uid = auth.currentUser?.uid  ?: "defaultUid"
            val db = FirebaseFirestore.getInstance()
            db.collection("NormalUsers").document(uid)
                .get()
                .addOnSuccessListener {
                        documentSnapshot ->
                    val userInfoData = documentSnapshot.toObject(UserInfoData::class.java)
                    _uiResponse.value = UiResponse(
                        isLoading = false,
                        isSuccess = true,
                        userInfoData = userInfoData)
                }
                .addOnFailureListener { exception ->
                    _uiResponse.value =uiResponse.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = exception.localizedMessage
                    )
                }
        }
    }

    fun fetchWishlist(){
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: "defaultUid"
            val db = FirebaseFirestore.getInstance()
            val userdoc = db.collection("NormalUsers").document(uid).get().await()
            val wishlistedProducts = userdoc.get("wishlist") as List<String>

            val productList = mutableListOf<ProductDataFlow>()
            for(productId in wishlistedProducts){
                val productDocRef = db.collection("Products").document(productId)
                val productDoc = productDocRef.get().await()
                val product = productDoc.toObject(ProductDataFlow::class.java)
                product?.let { productList.add(it) }
            }
            _products.value = productList
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid  ?: "defaultUid"
            val db = FirebaseFirestore.getInstance()
            db.collection("NormalUsers").document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val location = documentSnapshot.getString("Location")
                    if (location != null) {
                        val wishlistedProducts = documentSnapshot.get("wishlist") as List<String>

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

        documentRef.update("wishlist",FieldValue.arrayUnion(itemToAdd))
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
        latitude: Double,
        longitude: Double,
        username:String,
        itemname:String,
        location:String,
        Size:String,
        Detail:String,
        Price:Int,
        imageUri:List<Uri>){
        _uiResponse.value =uiResponse.value.copy(isLoading = true)

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
            "lat" to latitude,
            "long" to longitude,
            "imageUri" to urlToFirestore,
            "TimeStamp" to TimeStamp,
            "id" to uniqueImageId

        )
        try {
            db.collection("Products").document(uniqueImageId).set(Productdetails).await()
            _uiResponse.value =uiResponse.value.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null
            )
        } catch (e: Exception) {
            _uiResponse.value =uiResponse.value.copy(
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









}