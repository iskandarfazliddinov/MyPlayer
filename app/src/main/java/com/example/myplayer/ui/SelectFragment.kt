package com.example.myplayer.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.MediaStore.Audio
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myplayer.Models.UserData
import com.example.myplayer.R
import com.example.myplayer.databinding.FragmentSelectBinding
import kotlin.time.Duration.Companion.minutes


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var postion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            postion = it.getString("postion")
        }
    }

    private lateinit var binding: FragmentSelectBinding
    private lateinit var mediaPlayer: MediaPlayer
    lateinit var musicList: ArrayList<UserData>
    private lateinit var handler: Handler

    private lateinit var listMp3: ArrayList<String>
    private lateinit var imgs: ArrayList<String>
    private lateinit var nameA: ArrayList<String>
    private lateinit var musicN: ArrayList<String>

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectBinding.inflate(inflater, container, false)

        musicList = ArrayList()
        mediaPlayer = MediaPlayer()
        handler = Handler(Looper.getMainLooper())

        listMp3 = ArrayList()
        imgs = ArrayList()
        nameA = ArrayList()
        musicN = ArrayList()

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
                    val mp3 =
                        cursor?.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    listMp3.add(mp3.toString())
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val albumId =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val UriC = Uri.withAppendedPath(uri, albumId).toString()
                    imgs.add(UriC)
                    val title =
                        cursor?.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    musicN.add(title.toString())
                    val title2 =
                        cursor?.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    nameA.add(title2.toString())
                } while (cursor.moveToNext())
            cursor.close()
        }
        mediaPlayer = MediaPlayer()


        loads()
        binding.apply {
            btnForward.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition.plus(30000) ?: 0)
            }
            btnBackward.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition.minus(30000) ?: 0)
            }

            btnNext.setOnClickListener {
                if (postion!!.toInt() < listMp3.size - 1) {
                    postion = (postion!!.toInt() + 1).toString()
                    loads()
                }

            }
            btnBack.setOnClickListener {
                if (postion!!.toInt() > 0) {
                    postion = (postion!!.toInt() - 1).toString()
                    loads()
                }
            }
        }


        return binding.root
    }

    private fun loads() {
        binding.apply {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(listMp3[postion!!.toInt()])
            mediaPlayer.prepare()
            mediaPlayer.start()

            val maxDurations = mediaPlayer.duration
            seekBar.max = maxDurations
            handler.postDelayed(runnable, 1000)

            startMusic.setOnClickListener {
                if (mediaPlayer.isPlaying == true) {
                    btnPlay.visibility = View.VISIBLE
                    btnPause.visibility = View.INVISIBLE
                    mediaPlayer.pause()
                } else {
                    btnPlay.visibility = View.INVISIBLE
                    btnPause.visibility = View.VISIBLE
                    mediaPlayer.start()
                }
            }

            nameMusic.text = nameA[postion!!.toInt()]
            musicName.text = musicN[postion!!.toInt()]

            Glide.with(requireContext())
                .load(imgs[postion!!.toInt()])
                .apply(RequestOptions().placeholder(R.drawable.imgssss).centerCrop().centerCrop())
                .into(cardView)

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        mediaPlayer.seekTo(p1)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })
        }
    }

    private var runnable = object : Runnable {
        override fun run() {
            val currentPosition = mediaPlayer.currentPosition
            binding.seekBar.progress = currentPosition
            handler.postDelayed(this, 1000)

            binding.tvDuartions.text =
                "${mediaPlayer.mySecunds.div(600)}${mediaPlayer.mySecunds.div(60) % 10}:${mediaPlayer.mySecunds % 60 / 10}${mediaPlayer.mySecunds % 60 % 10} / ${
                    mediaPlayer.myCurrentSecunds.div(600)
                }${mediaPlayer.myCurrentSecunds.div(60) % 10}:${mediaPlayer.myCurrentSecunds % 60 / 10}${mediaPlayer.myCurrentSecunds % 60 % 10}"

        }

    }
    val MediaPlayer.mySecunds: Int
        get() {
            return this.currentPosition / 1000
        }

    val MediaPlayer.myCurrentSecunds: Int
        get() {
            return this.duration / 1000
        }

    companion object {


        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}