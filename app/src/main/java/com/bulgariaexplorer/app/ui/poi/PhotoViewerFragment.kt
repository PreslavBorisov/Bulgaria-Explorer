package com.bulgariaexplorer.app.ui.poi

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bulgariaexplorer.app.R
import androidx.navigation.fragment.findNavController
import coil.imageLoader
import coil.load
import com.bulgariaexplorer.app.databinding.FragmentPhotoViewerBinding
import com.bulgariaexplorer.app.utils.NavArgs
import java.io.File
import java.io.FileOutputStream

class PhotoViewerFragment : Fragment() {

    private var _binding: FragmentPhotoViewerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var imageUrl: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUrl = arguments?.getString(NavArgs.IMAGE_URL) ?: return

        binding.ivFullPhoto.load(imageUrl) {
            crossfade(true)
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.fabDownload.setOnClickListener {
            downloadAndSave()
        }
    }

    private fun downloadAndSave() {
        val url = imageUrl ?: return
        val context = requireContext()

        binding.fabDownload.isEnabled = false
        Toast.makeText(context, getString(R.string.photo_saving), Toast.LENGTH_SHORT).show()

        val loader = context.imageLoader
        val request = coil.request.ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .target { drawable ->
                val bitmap = (drawable as android.graphics.drawable.BitmapDrawable).bitmap
                saveBitmapToGallery(bitmap)
            }
            .build()
        loader.enqueue(request)
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val fileName = "BulgariaExplorer_${System.currentTimeMillis()}.jpg"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BulgariaExplorer")
                }
                val uri = requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
                )
                uri?.let {
                    requireContext().contentResolver.openOutputStream(it)?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                    }
                }
            } else {
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "BulgariaExplorer"
                )
                dir.mkdirs()
                val file = File(dir, fileName)
                FileOutputStream(file).use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                }
            }
            Toast.makeText(requireContext(), getString(R.string.photo_save_success), Toast.LENGTH_SHORT).show()
            binding.fabDownload.isEnabled = true
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(R.string.photo_save_error), Toast.LENGTH_SHORT).show()
            binding.fabDownload.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
