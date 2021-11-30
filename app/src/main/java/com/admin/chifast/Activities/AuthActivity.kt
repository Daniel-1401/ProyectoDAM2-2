package com.admin.chifast.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.admin.chifast.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        
        setup()
    }

    private fun setup() {
        title = "Autenticación"

        btnIngresar.setOnClickListener {
            if (username.text.isNotEmpty() && password.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        username.text.toString(),
                        password.text.toString()
                    ).addOnCompleteListener{
                        if (it.isSuccessful){
                            showHome(it.result?.user?.email?:"")
                        }else{
                            showAlert()
                        }
                    }
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ah producido un error en la autenticacion del usuario")
        builder.setPositiveButton("Acepar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String){
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(mainIntent)
    }

}