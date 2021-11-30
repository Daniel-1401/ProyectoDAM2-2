package com.admin.chifast.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import com.admin.chifast.Menu
import com.admin.chifast.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("menu").child(
            key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val menu: Menu? = dataSnapshot.getValue(Menu::class.java)
                if (menu != null) {
                    nameEditText.text = Editable.Factory.getInstance().newEditable(menu.name)
                    precioEditText.text = Editable.Factory.getInstance().newEditable(menu.precio)
                    descriptionEditText.text = Editable.Factory.getInstance().newEditable(menu.description)
                    urlEditText.text = Editable.Factory.getInstance().newEditable(menu.url)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        saveButton.setOnClickListener { v ->

            val name : String = nameEditText.text.toString()
            val precio : String = precioEditText.text.toString()
            val description: String = descriptionEditText.text.toString()
            val url: String = urlEditText.text.toString()

            myRef.child("name").setValue(name)
            myRef.child("precio").setValue(precio)
            myRef.child("description").setValue(description)
            myRef.child("url").setValue(url)

            finish()
        }
    }

}