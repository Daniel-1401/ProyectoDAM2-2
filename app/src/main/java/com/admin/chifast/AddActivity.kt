package com.admin.chifast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add.*

class AddActivity : AppCompatActivity() {

    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val myRef = database.getReference("menu")

        val name=nameEditText.text
        val precio=precioEditText.text
        val description=descriptionEditText.text
        val url=urlEditText.text

        saveButton.setOnClickListener { v ->
            val menu = Menu(name.toString(), precio.toString(), description.toString(), url.toString())
            myRef.child(myRef.push().key.toString()).setValue(menu)
            finish()
        }
    }
}