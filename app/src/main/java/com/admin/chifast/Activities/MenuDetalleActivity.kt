package com.admin.chifast.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.admin.chifast.Menu
import com.admin.chifast.R
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add.descriptionTextView
import kotlinx.android.synthetic.main.activity_add.nameTextView
import kotlinx.android.synthetic.main.activity_menu_detalle.*
import java.time.Instant

class MenuDetalleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_detalle)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("menu").child(
            key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val menu: Menu? = dataSnapshot.getValue(Menu::class.java)
                if (menu != null) {
                    nameProducto.text = menu.name.toString()
                    precioProducto.text = menu.precio.toString()
                    descriptionProducto.text = menu.description.toString()
                    images(menu.url.toString())
                }
            }


            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        btnBack.setOnClickListener{
            onBackPressed()
        }

    }

    private fun goEditLayout() {
        val editIntent = Intent(this, EditActivity::class.java)
        startActivity(editIntent)
    }


    private  fun images(url: String){
        Glide.with(this)
            .load(url)
            .into(posterImgeView)

        Glide.with(this)
            .load(url)
            .into(backgroundImageView)
    }
}