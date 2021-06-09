package com.example.android.firefly.ui.details

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.android.firefly.R
import com.example.android.firefly.constants.Constants.EXTERNAL_STORAGE_CODE
import com.example.android.firefly.databinding.FragmentDetailsBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var coordinatorLayoutDetails: CoordinatorLayout

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)
        val photo = args.photo

        coordinatorLayoutDetails = view.findViewById(R.id.coordinatorLayoutDetails)

//        val mColor=Color.parseColor("#0003DAC5")
//        val colorDrawable=ColorDrawable(mColor)
//        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(colorDrawable)

        with(binding) {
            Glide.with(this@DetailsFragment)
                .load(photo.urls.regular)
                .into(detailsImageView)
        }
        binding.downloadImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {  //Damn son, you don't need storage permission for Android 10 and above
                showSnackBar("Saving Image")
                downloadImage(args.photo.links.download)
            } else {
                checkForPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    EXTERNAL_STORAGE_CODE
                )
            }
        }
    }

    /*----------------Permission code starts here-----------------------*/
    private fun checkForPermissions(permission: String, requestCode: Int) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                showSnackBar("Saving Image")
                downloadImage(args.photo.links.download)
            }
            shouldShowRequestPermissionRationale(permission) -> showDialog(permission, requestCode)

            else -> {
                // You can directly ask for the permission.
                requestPermissions(arrayOf(permission), EXTERNAL_STORAGE_CODE)
            }
        }
    }

    private fun showDialog(permission: String, requestCode: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setMessage(R.string.storage_permission_dialog)
            setTitle(R.string.storage_permission_title)
            setPositiveButton("Ok") { dialog, which ->
                requestPermissions(arrayOf(permission), EXTERNAL_STORAGE_CODE)
            }
            setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            EXTERNAL_STORAGE_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    showSnackBar("Saving Image")
                    downloadImage(args.photo.links.download)
                } else {
                    showSnackBar("Sorry, this feature is not available. To use, accept storage permission")
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }

    }

    /*----------------Permission code ends here-----------------------*/

    private fun downloadImage(imageDownloadURL: String) {
        Glide.with(requireContext())
            .asBitmap()
            .load(imageDownloadURL)
            .format(DecodeFormat.PREFER_ARGB_8888)
            .into(object : CustomTarget<Bitmap>(1000, 2000) {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    GlobalScope.launch {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) savePhoto(resource)
                        else savePhotoQ(resource)
                    }

                }
            })

    }

    private suspend fun savePhoto(mBitmap: Bitmap) {
        val filename = "${R.string.app_name}_" + args.photo.id + ".png"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "/Firefly"
        )
        if (!storageDir.mkdir()) {
            showSnackBar("Failed to create directory")
            return
        }
        val imageFile = File(storageDir, filename)
        try {
            val outputStream = FileOutputStream(imageFile)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            showSnackBar("Failed to save Image")
            return
        }
        showSnackBar("Image Saved")
    }

    private suspend fun savePhotoQ(mBitmap: Bitmap) {
        val filename = "${R.string.app_name}_" + args.photo.id + ".png"
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Firefly")
        }
        val uri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        try {
            resolver.openOutputStream(uri!!).use { outputStream ->
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream?.close()
            }
        } catch (e: IOException) {
            showSnackBar("Failed to save Image")
            return
        }
        showSnackBar("Image Saved")


    }

    private fun showSnackBar(text: String) {
        Snackbar.make(coordinatorLayoutDetails, text, Snackbar.LENGTH_SHORT)
            .setAction("Close") {
            }
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
