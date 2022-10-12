package com.example.fotografpaylas

import android.annotation.SuppressLint
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.convertTo
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_fotograf_paylasma.*
import kotlinx.android.synthetic.main.activity_haberler.*
import kotlinx.android.synthetic.main.recycler_row.*
import kotlinx.android.synthetic.main.recycler_row.view.*
import java.util.zip.Inflater

class HaberlerActivity : AppCompatActivity() {
    var secilenGorsel: Uri?=null
    var secilenBitmap: Bitmap?=null
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseFirestore
    private lateinit var recyclerViewAdapter: HaberRecyclerAdapter
    var postlistesi= ArrayList<Post>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_haberler)
        auth=FirebaseAuth.getInstance()
        database= FirebaseFirestore.getInstance()
        verileriAl()
        val layoutManager=LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager
        recyclerViewAdapter=HaberRecyclerAdapter(postlistesi)
        recyclerView.adapter=recyclerViewAdapter

    }

    fun onDefaultToggleClick(view: View){
        Toast.makeText(this,"Beğendin!!",Toast.LENGTH_LONG).show()
    }
    fun gulmeclick(view: View){
        Toast.makeText(this,"Bence Komik ",Toast.LENGTH_LONG).show()
    }
    fun yorumclick(view: View){
        Toast.makeText(this,"Yorum yap",Toast.LENGTH_LONG).show()
    }
    fun profilsec(view: View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //izni almamısız
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE ),1)

        }else{
            val galeriintent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                    profilfoto.setImageBitmap(secilenBitmap)
                } else {
                    secilenBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                    profilfoto.setImageBitmap(secilenBitmap)
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun verileriAl(){
        database.collection("Yeni").orderBy("tarih",Query.Direction.DESCENDING).addSnapshotListener{ snapshot,exception->
            if(exception!=null){
                Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if(snapshot!=null){
                    if(!snapshot.isEmpty){
                        val documents=snapshot.documents
                        postlistesi.clear()
                        for(document in documents){
                            val kullanicimaili=document.get("maili") as String
                            val kullaniciyorumu=document.get("yorum") as String
                            val gorselurl=document.get("gorselurl") as String
                            val kullaniciadi=document.get("kullaniciadi") as String
                            var indirilenpost= Post(kullanicimaili,kullaniciyorumu,gorselurl,kullaniciadi)
                            postlistesi.add(indirilenpost)
                        }
                        recyclerViewAdapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate=menuInflater
        menuInflater.inflate(R.menu.secenekler_menusu,menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId==R.id.fotograf_paylas){
                //fotograf yapma aktivitesine gidilecek
                val intent=Intent(this,fotografPaylasmaActivity::class.java)
                startActivity(intent)
            }
             else if (item.itemId==R.id.cikis_yap) {
                 auth.signOut()
                 val intent=Intent(this,KullaniciActivity::class.java)
                 startActivity(intent)
                 finish()
        }else if(item.itemId==R.id.hakkinda){
            Toast.makeText(this,"Created by Evşin Dara",Toast.LENGTH_LONG).show()
        }
        else if(item.itemId==R.id.kampanya){
            val intent=Intent(this,Kampanyalar::class.java)
                startActivity(intent)


        }

        return super.onOptionsItemSelected(item)
    }
}