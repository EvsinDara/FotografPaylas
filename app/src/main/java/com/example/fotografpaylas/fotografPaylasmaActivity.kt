package com.example.fotografpaylas

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask.TaskSnapshot
import kotlinx.android.synthetic.main.activity_fotograf_paylasma.*
import java.util.UUID

class fotografPaylasmaActivity : AppCompatActivity() {
    var secilenGorsel:Uri?=null
    var secilenBitmap:Bitmap?=null
    private lateinit var storage:FirebaseStorage
    private  lateinit var auth:FirebaseAuth
    private  lateinit var database:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotograf_paylasma)
        storage=FirebaseStorage.getInstance()
        auth=FirebaseAuth.getInstance()
        database= FirebaseFirestore.getInstance()
        
    }

    fun paylas(view:View){
        val uuid=UUID.randomUUID()
        val gorselismi="${uuid}.jpg"
        val reference=storage.reference
        val gorselreference=reference.child("images").child(gorselismi)
        if(secilenGorsel!=null) {
            gorselreference.putFile(secilenGorsel!!).addOnSuccessListener { TaskSnapshot ->
                val yuklenengorselreference =
                    FirebaseStorage.getInstance().reference.child("images")
                        .child(gorselismi)
                yuklenengorselreference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadurl = uri.toString()
                    val kullaniciemaili = auth.currentUser!!.email.toString()
                    val kullaniciyorumu = yorumText.text.toString()
                    val kullaniciadi=kullaniciadiText.text.toString()
                    val tarih = Timestamp.now()
                    //database
                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("gorselurl", downloadurl)
                    postHashMap.put("kullaniciadi",kullaniciadi)
                    postHashMap.put("maili", kullaniciemaili)
                    postHashMap.put("yorum", kullaniciyorumu)
                    postHashMap.put("tarih", tarih)

                    database.collection("Yeni").add(postHashMap).addOnCompleteListener { Task ->
                        if (Task.isSuccessful) {
                            finish()
                        }
                    }.addOnFailureListener { Exception ->
                        Toast.makeText(
                            applicationContext,
                            Exception.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }.addOnFailureListener { Exception ->
                    Toast.makeText(
                        applicationContext,
                        Exception.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun gorselSec(view: View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //izni almamısız
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE ),1)

        }else{
            val galeriintent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriintent,2)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode==1){
            //
            if(grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //izin verilince yapılıcaklar
                val galeriintent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriintent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            secilenGorsel = data.data
            if (secilenGorsel != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver, secilenGorsel!!)
                    secilenBitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(secilenBitmap)
                } else {
                    secilenBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                    imageView.setImageBitmap(secilenBitmap)
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}