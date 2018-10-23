package com.alynva.doarei.doarei

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_account.*
import java.io.File
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class CreateAccountActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PHOTO = 200
        var mAuth = FirebaseAuth.getInstance()
        var db = FirebaseFirestore.getInstance()
    }

    var actual_photo_path:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        btn_back_activity.setOnClickListener {
            this.finish()
        }

        change_photo.setOnClickListener {
            capturarFoto()
        }

        btn_create_acc.setOnClickListener { criarConta() }

        val adapter_account_types = ArrayAdapter.createFromResource(this, R.array.account_types, android.R.layout.simple_spinner_item)
        adapter_account_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_account_type.setAdapter(adapter_account_types)
        spinner_account_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(parent?.getItemAtPosition(position)) {
                    "Pessoa física" -> {
                        ipt_group_cpf.visibility = View.VISIBLE
                        ipt_group_cnpj.visibility = View.GONE

                        ipt_group_age.visibility = View.VISIBLE
                    }
                    "Pessoa jurídica" -> {
                        ipt_group_cpf.visibility = View.GONE
                        ipt_group_cnpj.visibility = View.VISIBLE

                        ipt_group_age.visibility = View.GONE
                    }

                    else -> {
                        ipt_group_cpf.visibility = View.VISIBLE
                        ipt_group_cnpj.visibility = View.VISIBLE

                        ipt_group_age.visibility = View.VISIBLE
                    }
                }
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
            }
        }
    }

    private fun criarConta() {
        val email = ipt_email.text.toString()
        val password = ipt_pass.text.toString()


        val tipo = spinner_account_type.selectedItem.toString()
        val nome = ipt_name.text.toString()
        val cpf = ipt_cpf.text.toString()
        val age = ipt_age.text.toString()
        val cnpj = ipt_cnpj.text.toString()
        val adress = ipt_adress.text.toString()
        val phone = ipt_phone.text.toString()

        var isTipo1 = tipo == resources.getStringArray(R.array.account_types).get(0)
        var isTipo2 = tipo == resources.getStringArray(R.array.account_types).get(1)

        if (nome.isBlank() || (isTipo1 && (cpf.isBlank() || age.isBlank())) || (isTipo2 && cnpj.isBlank()) || adress.isBlank() || phone.isBlank()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val auth_user = mAuth.currentUser


                        val bd_user = HashMap<String, Any>()
                        bd_user.put("tipo", tipo)
                        bd_user.put("nome", nome)
                        bd_user.put("email", email)
                        if (isTipo1) {
                            bd_user.put("cpf", cpf)
                            bd_user.put("age", age)
                        } else if (isTipo2) {
                            bd_user.put("cnpj", cnpj)
                        }
                        bd_user.put("adress", adress)
                        bd_user.put("phone", phone)

                        db.collection("users")
                                .add(bd_user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Usuário registrado :)", Toast.LENGTH_LONG).show()
                                    startMainActivity()
                                }.addOnFailureListener {
                                    exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                                }
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.getCurrentUser()
        if (currentUser != null) {
            startMainActivity()
        }
    }

    private fun capturarFoto() {
        val intent_photo = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            val photo_file = MontaArquivo()
            val photo_uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", photo_file)
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri)
            startActivityForResult(intent_photo, REQUEST_PHOTO)
        } else
            Toast.makeText(this, "Não é possível tirar uma foto.", Toast.LENGTH_SHORT).show()
    }

    private fun MontaArquivo(): File {
        val file_name = "${System.currentTimeMillis()}"
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photo_file = File.createTempFile(file_name, ".png", dir)

        actual_photo_path = photo_file.absolutePath

        return photo_file
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Toast.makeText(this, "Image recebida ${resultCode} ${REQUEST_PHOTO} ${Activity.RESULT_OK}", Toast.LENGTH_LONG).show()
        if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
            val photo_file = File(actual_photo_path)

            var bitmap = if (photo_file.exists()) {
                BitmapFactory.decodeFile(photo_file.absolutePath)
            } else {
                data?.extras?.get("data") as Bitmap
            }
            bitmap = verificaRotacao(bitmap)
            first_user_photo.setImageBitmap(bitmap)
            first_user_photo.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun verificaRotacao(bitmap: Bitmap?): Bitmap? {
        val ei = ExifInterface(actual_photo_path)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 ->
                rotateImage(bitmap, 90F)

            ExifInterface.ORIENTATION_ROTATE_180 ->
                rotateImage(bitmap, 180F);

            ExifInterface.ORIENTATION_ROTATE_270 ->
                rotateImage(bitmap, 270F);

            else ->
                bitmap;

        }
    }

    private fun rotateImage(source: Bitmap?, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source!!.width, source.height,
                matrix, true)
    }
}
