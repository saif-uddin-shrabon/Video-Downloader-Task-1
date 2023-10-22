package com.cmed.myapplication.View

import android.app.Notification
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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.cmed.myapplication.R
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

    // declaring variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "Download_Channel"

    var progress : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentDownladBinding.inflate(inflater,container,false)
        createNotificationChannel()
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding?.downloadbtn?.setOnClickListener {

            val filename = System.currentTimeMillis().toString().replace(":",".")+".mp4"
            downloadFile("https://video-downloads.googleusercontent.com/ADGPM2n0JkRLtH-0xCN7BFPefeU3P2jsAbQK0DP_vYWjvHgzJfL_CLrrauwQjmoLIKU-kyjDloyq6KuYpyqzd4bVTOCug7RS6H9LdlirMDB_9MBs03dq5Xhdcbw9JcFTXxuZc3QKkLwYLXdYBzuW0UH_-1MRWFIRLYQN9Xpa0KJ-ODrCNQXvAVfhvY1PYfX4c2tvuJVmsIbjHUNHzcW7SbGn9yIh3PDX6mm7MUKmmwCHG7Cvxv0R8L29-9IFXcjouB6B6aRNJwQr_YQdXTQpZL4VmpUoN8csSWtitAa7Nnd0CZQJVvibyBsyvqP3xB1Mzm7yuopd-kGXtjmj9LG--4KwyoUhzmOlhOiIyJE0IX0z21DRA1atMVE-lKsjEipDlR5MPSTHZZ7jID3JFy06YCK05wkLjhvh5CuHvJYPn_ehvThbkde2erxgXuFt_dOsNjdicyacZse4urv0YM_Ezz4DOHGq_Xm4QRtSZf-FaV20QykL8XOi5wP9YylzIZxANQFVmG-2R4N5A9AwdW0zT0CMdKYxx3EaVtYSN9R4jeXQNKEtltQnkaV3n2GDIyzvbVMFe_y9YhLVUcgf89wtMaTh_EGNAPqaB9dZ09MyVVuKumCBSZGHoW3hJXb3oNS6mCQPOH5iFaXkbhGAU7P23mX27DW1XeW21u9THgq_RT7akuIeCOj9ckbfXwH4_qNUnEj-YTigC2Jffr0roBfbzImp-5-HgWq-PLuCOQsGYOud4vZSfMwzkgNMfd7oVCH18b12geMfncQ4it1jPBe7L6sV1BB9lG1uf294Au7raddQ7R8nGeGEMRXuCeIOgz7LUmfVsKYexV72XimDLYzOdXxxEyZyE1buiYdpBcKAK8L0wMkj6xfDrFUDYfQJ8uFcMEcQA4T7-KxQY2qLXjwm4j1Niua23lRxe6ItFdRvOgGNO9idSWlH8vS5pRKOlmgsB8PFanY6ZWeZIxB04IWYuYytpDM8Vepvy5h0LGD_H_am0u_YOCIdW0PyKOMgDj-YACdvqrAjAVoqIHkfX9Z-DyZuRy8loWdPos2phDBUrrMqk22_D_r5Y6lyY_OcKbc-pvZhHCHVvMeZNj8eH8ZTmam0pt1d9FYmGhpSn9TSE760UN9_abvEcSn9bkCaZHt3KmzSJjoA6MwNJ7ExAJWLu8Ac53plWisESQ", filename)
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Download Channel"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
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


                    outputStream.write(buffer, 0, bytes)
                    bytes = inputStream.read(buffer)
                }

                outputStream.close()
                inputStream.close()


            }

        }

    }

    override fun onResume() {
        super.onResume()


    }

    override fun onPause() {
        super.onPause()
        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationLayoutExpanded = RemoteViews(requireContext().packageName, R.layout.notification_large)



        val intent = Intent(requireContext(), MainActivity::class.java) // Replace with your main activity
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val customNotification =
            progress?.let {
                NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon) // Replace with your icon
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setContentTitle("Download")
                    .setContentText("Downloading....")
                    .setProgress(100,it,false)
                    .setOngoing(true)
                    .build()
            }

        notificationManager.notify(NOTIFICATION_ID, customNotification)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}