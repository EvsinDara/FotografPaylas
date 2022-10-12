package com.example.fotografpaylas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class KullaniciActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth=FirebaseAuth.getInstance()
        val guncelkullanici=auth.currentUser
        if (guncelkullanici!=null){
            val intent=Intent(this,HaberlerActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun girisYap(view: View){

            auth.signInWithEmailAndPassword(emailText.text.toString(),passwordText.text.toString()).addOnCompleteListener { Task->
                if(Task.isSuccessful){
                    val guncelkullanici=auth.currentUser?.email.toString()
                    Toast.makeText(this,"Hoşgeldiniz:${guncelkullanici}",Toast.LENGTH_LONG).show()
                    val intent=Intent(this,HaberlerActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }.addOnFailureListener { Exception->
                Toast.makeText(this,Exception.localizedMessage,Toast.LENGTH_LONG).show()

            }
    }

    fun kayitOl(view: View){
        val email=emailText.text.toString()
        val sifre=passwordText.text.toString()
        auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener { task->
            if (task.isSuccessful){
                //diger aktiviteye
                val intent=Intent(this,HaberlerActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            Exception->
            Toast.makeText(applicationContext,Exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }

}