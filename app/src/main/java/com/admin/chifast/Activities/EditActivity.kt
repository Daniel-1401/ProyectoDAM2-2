package com.admin.chifast.Activities

import android.content.Intent
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_edit.btnAgregaImg
import kotlinx.android.synthetic.main.activity_edit.descriptionEditText
import kotlinx.android.synthetic.main.activity_edit.nameEditText
import kotlinx.android.synthetic.main.activity_edit.precioEditText
import kotlinx.android.synthetic.main.activity_edit.saveButton

class EditActivity : AppCompatActivity() {

    private val GALLERY_INTENT = 1
    private val database = Firebase.database
    private var FileUri: Uri? = null
    private var urlImagen= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val key = intent.getStringExtra("key")
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("menu").child(
            key.toString()
        )


        btnAgregaImg.setOnClickListener {
            fileUpload()
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val menu: Menu? = dataSnapshot.getValue(Menu::class.java)
                if (menu != null) {
                    nameEditText.text = Editable.Factory.getInstance().newEditable(menu.name)
                    precioEditText.text = Editable.Factory.getInstance().newEditable(menu.precio)
                    descriptionEditText.text = Editable.Factory.getInstance().newEditable(menu.description)
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

            myRef.child("name").setValue(name)
            myRef.child("precio").setValue(precio)
            myRef.child("description").setValue(description)
            myRef.child("url").setValue(urlImagen)

            finish()
        }
    }
    private fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, GALLERY_INTENT)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_INTENT) {
            if (resultCode == RESULT_OK) {
                FileUri = data!!.data!!
                val Folder: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("menu")
                val file_name: StorageReference = Folder.child("imagen" + FileUri!!.lastPathSegment)
                file_name.putFile(FileUri!!).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                        urlImagen = java.lang.String.valueOf(uri)
                        Log.d("Mensaje", "Se subió correctamente")
                    }
                }
            }
        }
    }

}