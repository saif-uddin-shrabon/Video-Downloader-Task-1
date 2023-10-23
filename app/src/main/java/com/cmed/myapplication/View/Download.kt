package com.cmed.myapplication.View


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews

import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.viewModels
import com.cmed.myapplication.R
import com.cmed.myapplication.Viewmodel.DownloadViewModel

import com.cmed.myapplication.databinding.FragmentDownladBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Download : Fragment() {

    private var _binding: FragmentDownladBinding? = null
    private val binding get() = _binding
    private val downloadViewModel by viewModels<DownloadViewModel> ()

    val STORAGE_DIRECTORY = "/Download/DEMC"

    var progress : Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDownladBinding.inflate(inflater, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(

                requireActivity(),
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        downloadViewModel.createNotificationChannel(requireContext())

            return binding?.root
        }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding?.downloadbtn?.setOnClickListener {


            val filename = System.currentTimeMillis().toString().replace(":",".")+".mp4"
            downloadFile("https://www.shutterstock.com/shutterstock/videos/1102318137/preview/stock-footage-mud-and-dust-flying-green-screen-effect-video-dust-dust-blown-green-screen-video-gust-and-dust.webm", filename)
        }

    }




    fun downloadFile(url: String, fileName:String){
        val storageDirectory = Environment.getExternalStorageDirectory().toString() + STORAGE_DIRECTORY + "/${fileName}"
        val file = File(Environment.getExternalStorageDirectory().toString()+ STORAGE_DIRECTORY)

        if(!file.exists()){
            file.mkdirs()
        }

        GlobalScope.launch(Dispatchers.IO){
            val url = URL(url)
            val connection  = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept-Encoding", "identity")
            connection.connect()

            if(connection.responseCode in 200..299){

                val fileSize = connection.contentLength

                withContext(Dispatchers.Main){
                    binding?.progressBar?.progress = 0
                }


                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(storageDirectory)

                var byteCopy : Long = 0
                var buffer = ByteArray(1024)
                var bytes =  inputStream.read(buffer)

                while (bytes >= 0 ){
                    byteCopy += bytes

                     progress = (byteCopy.toFloat()/ fileSize.toFloat() * 100).toInt()

                    withContext(Dispatchers.Main){
                        binding?.progressBar?.progress = progress as Int
                        binding?.progresttext?.text = "${progress}%"

                    }

                    downloadViewModel.updateNotification(requireContext(), progress!!)


                    outputStream.write(buffer, 0, bytes)
                    bytes = inputStream.read(buffer)
                }

                outputStream.close()
                inputStream.close()


            }

        }

    }




    override fun onPause() {
        super.onPause()
          downloadViewModel.updateNotification(requireContext(), progress ?: 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}