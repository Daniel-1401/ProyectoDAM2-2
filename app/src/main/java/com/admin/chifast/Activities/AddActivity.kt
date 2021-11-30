package com.admin.chifast.Activities

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_menu_detalle.*

class AddActivity : AppCompatActivity() {

    private val database = Firebase.database
    private val File = 1
    val myRef = database.getReference("imgPlatos")

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val myRef = database.getReference("menu")

        val name=nameEditText.text
        val precio=precioEditText.text
        val description=descriptionEditText.text
        //val url=urlEditText.text --, url.toString()

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

    private fun saveDate(name:String, precio:String, description:String ) {
        val menu = Menu(name, precio, description)
        myRef.child(myRef.push().key.toString()).setValue(menu)
        finish()
    }

    private fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, File)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == File) {
            if (resultCode == RESULT_OK) {
                val FileUri = data!!.data
                val Folder: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("ImgPlatos")
                val file_name: StorageReference = Folder.child("file" + FileUri!!.lastPathSegment)
                file_name.putFile(FileUri).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                        val hashMap =
                            HashMap<String, String>()
                        hashMap["link"] = java.lang.String.valueOf(uri)
                        myRef.setValue(hashMap)
                        Log.d("Mensaje", "Se subió correctamente")
                    }
                }
            }
        }
    }
}