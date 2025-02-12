package com.buzzware.radidodriver.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.location.Location
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.buzzware.radidodriver.R
import com.buzzware.radidodriver.classes.FragmentListener
import com.buzzware.radidodriver.databinding.AlertMarkDeliverLayoutBinding
import com.buzzware.radidodriver.databinding.FragmentMapBinding
import com.buzzware.radidodriver.model.DirectionsResponse
import com.buzzware.radidodriver.service.DirectionsApiService
import com.buzzware.rapidouser.utils.UserSession
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.util.UUID

class MapFragment(val latitude: Double, val longitude: Double, val orderID: String) : Fragment(),
    OnMapReadyCallback {

    lateinit var binding: FragmentMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fragmentContext: Context
    private lateinit var dialogBinding: AlertMarkDeliverLayoutBinding

    private var medicineURI: Uri? = null
    private var parcelURI: Uri? = null
    private var signatureURI: Uri? = null

    private lateinit var startLocation: LatLng
    private lateinit var endLocation: LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val MEDICINE_REQ_CODE = 100
        const val PARCEL_REQ_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)


        setView()
        setListener()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragmentContext)


        return binding.root
    }

    private fun setView() {

    }

    private fun setListener() {

        binding.markDeliveredTV.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogBinding =
            AlertMarkDeliverLayoutBinding.inflate(LayoutInflater.from(requireActivity()))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.apply {
            linearLayout2.setOnClickListener {
                openGalleryOrFilePicker(MEDICINE_REQ_CODE,12f,12f)
            }


            linearLayout4.setOnClickListener {
                openGalleryOrFilePicker(PARCEL_REQ_CODE,12f,5f)
            }

            orderNowTV.setOnClickListener {
                val signature: Bitmap = dialogBinding.signatureView.getSignatureBitmap()

                if (medicineURI == null) {
                    Toast.makeText(
                        fragmentContext,
                        "medicine image should not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (parcelURI == null) {
                    Toast.makeText(
                        fragmentContext,
                        "parcel image should not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (dialogBinding.signatureView.isEmpty) {
                    Toast.makeText(
                        fragmentContext,
                        "signature image should not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                updateOrder(parcelURI!!, medicineURI!!, signature) {
                    (activity as FragmentListener).loadFragment("Home")
                    dialog.dismiss()
                }
            }

            clearTV.setOnClickListener {
                signatureView.clear()
            }
        }

        dialog.show()
    }

    private fun updateOrder(
        parsalImageURI: Uri,
        medicineImageURI: Uri,
        signatureImageURI: Bitmap,
        callback: () -> Unit
    ) {
        dialogBinding.progressBar.visibility = View.VISIBLE
        var parcelImage = ""
        var medicineImage = ""
        var signatureImage = ""

        val parcelStorage =
            FirebaseStorage.getInstance().reference.child("OrderRequest/Image/${UserSession.user.id}/Parcel${UUID.randomUUID()}.jpg")
        val parcelUploadTask = parcelStorage.putFile(parsalImageURI)

        val medicineStorage =
            FirebaseStorage.getInstance().reference.child("OrderRequest/Image/${UserSession.user.id}/Medicine${UUID.randomUUID()}.jpg")
        val medicineUploadTask = medicineStorage.putFile(medicineImageURI)

        val signatureStorage =
            FirebaseStorage.getInstance().reference.child("OrderRequest/Image/${UserSession.user.id}/Signature${UUID.randomUUID()}.jpg")
        val signatureUploadTask = signatureStorage.putBytes(bitmapToByteArray(signatureImageURI))

        medicineUploadTask.addOnSuccessListener {
            medicineStorage.downloadUrl.addOnSuccessListener {
                medicineImage = it.toString()

                parcelUploadTask.addOnSuccessListener {
                    parcelStorage.downloadUrl.addOnSuccessListener {
                        parcelImage = it.toString()

                        signatureUploadTask.addOnSuccessListener {
                            signatureStorage.downloadUrl.addOnSuccessListener {
                                signatureImage = it.toString()

                                val updateOrder = hashMapOf(
                                    "parsalImage" to parcelImage,
                                    "reciverImage" to medicineImage,
                                    "signatureImage" to signatureImage,
                                    "status" to "Payment Approval"
                                )
                                Firebase.firestore.collection("OrderRequest").document(orderID)
                                    .update(updateOrder as Map<String, Any>).addOnSuccessListener {
                                        dialogBinding.progressBar.visibility = View.GONE
                                        callback.invoke()
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun openGalleryOrFilePicker(requestCode: Int, width: Float, height: Float) {
        ImagePicker.with(this)
            .crop(width, height)
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                MEDICINE_REQ_CODE -> {
                    medicineURI = data!!.data
                    Log.d("imageURI", "onActivityResult: Medicine: $medicineURI")
                    if (dialogBinding != null) {
                        dialogBinding.medicineIv.visibility = View.VISIBLE
                        Glide.with(fragmentContext).load(medicineURI).into(dialogBinding.medicineIv)
                        dialogBinding.medicineIvGoup.visibility = View.GONE
                    }
                }

                PARCEL_REQ_CODE -> {
                    parcelURI = data!!.data
                    Log.d("imageURI", "onActivityResult: parcelURI: $parcelURI")
                    if (dialogBinding != null) {
                        dialogBinding.parcelIv.visibility = View.VISIBLE
                        Glide.with(fragmentContext).load(parcelURI).into(dialogBinding.parcelIv)
                        dialogBinding.parcelIvGoup.visibility = View.GONE

                    }
                }
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(fragmentContext, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        endLocation = LatLng(latitude, longitude)
        val zoomLevel = 14f
        mMap?.addMarker(
            MarkerOptions().icon(
                setIcon(
                    requireActivity(),
                    R.drawable.ic_drop_off_marker
                )
            ).position(endLocation)
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endLocation, zoomLevel))

        if (ContextCompat.checkSelfPermission(
                fragmentContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    startLocation = LatLng(location.latitude, location.longitude)
                    getDirections()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
        }
    }

    private fun getDirections() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DirectionsApiService::class.java)

        val call = service.getDirections(
            "${startLocation.latitude},${startLocation.longitude}",
            "${endLocation.latitude},${endLocation.longitude}",
            getString(R.string.GOOGLE_MAP_API_KEY)
        )

        call.enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val route = response.body()?.routes?.firstOrNull()
                    val polyline = route?.overviewPolyline?.points
                    if (polyline != null) {
                        drawRoute(polyline)
                    }
                } else {
                    Log.e("MapsActivity", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Log.e("MapsActivity", "API call failed: ${t.message}")
            }
        })
    }

    private fun drawRoute(encodedPolyline: String) {
        val decodedPath = PolyUtil.decode(encodedPolyline)

        val polylineOptions = PolylineOptions()
            .addAll(decodedPath)
            .width(5f)
            .color(Color.RED)
            .geodesic(true)

        mMap.addPolyline(polylineOptions)
    }

    private fun setIcon(context: Activity, drawableID: Int): BitmapDescriptor {
        val drawable = ActivityCompat.getDrawable(context, drawableID)
        drawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}