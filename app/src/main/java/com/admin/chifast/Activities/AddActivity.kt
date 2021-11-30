package com.admin.chifast.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.admin.chifast.Menu
import com.admin.chifast.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_add.btnBack

class AddActivity : AppCompatActivity() {

    private val database = Firebase.database
    private val GALLERY_INTENT = 1
    val myRef = database.getReference("menu")
    private var FileUri: Uri? = null
    private var urlImagen= ""

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)


        val name=nameEditText.text
        val precio=precioEditText.text
        val description=descriptionEditText.text


        saveButton.setOnClickListener { v ->
            saveDate(name.toString(), precio.toString(), description.toString())
        }
        btnBack.setOnClickListener{
            onBackPressed()
        }
        btnAgregaImg.setOnClickListener {
            fileUpload()
        }
    }

    private fun saveDate(name:String, precio:String, description:String,  ) {
        val menu = Menu(name, precio, description,urlImagen)
        myRef.child(myRef.push().key.toString()).setValue(menu)
        finish()
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