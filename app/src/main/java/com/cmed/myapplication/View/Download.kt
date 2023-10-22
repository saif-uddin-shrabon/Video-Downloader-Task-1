package com.cmed.myapplication.View

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.cmed.myapplication.databinding.FragmentDownladBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Download : Fragment() {

    private var _binding: FragmentDownladBinding? = null
    private val binding get() = _binding

    val STORAGE_DIRECTORY = "/Download/DEMC"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentDownladBinding.inflate(inflater,container,false)
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

                    val progress = (byteCopy.toFloat()/ fileSize.toFloat() * 100).toInt()

                    withContext(Dispatchers.Main){
                        binding?.progressBar?.progress = progress
                        binding?.progresttext?.text = "${progress}%"
                    }

                    outputStream.write(buffer, 0, bytes)
                    bytes = inputStream.read(buffer)
                }

                outputStream.close()
                inputStream.close()


            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}