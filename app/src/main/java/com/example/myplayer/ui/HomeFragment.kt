package com.example.myplayer.ui

import android.Manifest
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.myplayer.Adapters.UserAdapter
import com.example.myplayer.Models.UserData
import com.example.myplayer.R
import com.example.myplayer.databinding.FragmentHomeBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), PermissionListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var userAdapter: UserAdapter
    private lateinit var userData: ArrayList<UserData>

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        loadDexter()

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun loadDexter() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(this)
            .check()
    }

    @SuppressLint("Range")
    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
        userData = ArrayList()
        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_EXPIRES,
            MediaStore.Audio.Media.VOLUME_NAME
        )
        val selec = MediaStore.Audio.Media.IS_MUSIC + " !=0"
        val cursor = requireActivity().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, selec, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC", null
        )

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val title =
                        cursor?.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val title2 =
                        cursor?.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val albumId =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val mp3 =
                        cursor?.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

                    val uri = Uri.parse("content://media/external/audio/albumart")

                    val UriC = Uri.withAppendedPath(uri, albumId).toString()



                    userData.add(
                        UserData(
                            aftorName = title2.toString(),
                            musicName = title.toString(),
                            UriC,
                            mp3.toString()
                        )
                    )
                } while (cursor.moveToNext())
            cursor.close()
        }


        userAdapter = UserAdapter(requireContext(), userData, { data, pos ->
            val bundle = Bundle()

            bundle.putString("postion", pos.toString())

            findNavController().navigate(R.id.action_homeFragment_to_selectFragment, bundle)
        })

        binding.reyc.adapter = userAdapter
        userAdapter.notifyDataSetChanged()

        mediaPlayer = MediaPlayer()
    }

    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Ruhsat berish")
        dialog.setMessage("Siz ruxsat bermasangiz bu dasturdan foydalana olmaysiz !")
        dialog.setPositiveButton("RUXSAT BERISH") { _, _ ->
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
        dialog.setNegativeButton("RAD ETISH") { _, _ ->
            dialog.create().dismiss()
        }
        dialog.show()
    }

    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {

    }
}