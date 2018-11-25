package com.alynva.doarei.doarei

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_create_account.*
import java.io.File
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreateAccountActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PHOTO = 200
        const val REQUEST_LOC = 201
        var mAuth = FirebaseAuth.getInstance()!!
        var db = FirebaseFirestore.getInstance()
        var storage = FirebaseStorage.getInstance()
    }

    private var actualPhotoPath:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        btn_back_activity.setOnClickListener {
            this.finish()
        }

        change_photo.setOnClickListener {
            capturarFoto()
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission()
        }

        btn_create_acc.setOnClickListener { criarConta() }

        val adapterAccountTypes = ArrayAdapter.createFromResource(this, R.array.account_types, android.R.layout.simple_spinner_item)
        adapterAccountTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_account_type.adapter = adapterAccountTypes
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQUEST_LOC -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                } else {
                    obtemLocalizacao(LocationServices.getFusedLocationProviderClient(this)) { latitude, longitude -> setLocation(latitude, longitude) }
                }
            }
        }
    }

    private fun setLocation(latitude: Double?, longitude: Double?) {
        ipt_adress.keyListener = null
        ipt_adress.setText("$latitude, $longitude")
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                           ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        ){//Can add more as per requirement

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_LOC)
        } else {
            obtemLocalizacao(LocationServices.getFusedLocationProviderClient(this)) { latitude, longitude -> setLocation(latitude, longitude) }
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

        val fb = ipt_fb.text.toString()
        val fbRegex = """^(?:(?:http(?:s)?://)?(?:www.)?facebook\.com/)?([a-zA-Z0-9.]+).*$""".toRegex(RegexOption.MULTILINE)
        val fbResult = fbRegex.find(fb)
        val (fbId) = fbResult!!.destructured

        val tt = ipt_tt.text.toString()
        val ttRegex = """^(?:(?:http(?:s)?://)?(?:www.)?twitter\.com/)?([a-zA-Z0-9_]{1,15}).*$""".toRegex(RegexOption.MULTILINE)
        val ttResult = ttRegex.find(tt)
        val (ttId) = ttResult!!.destructured

        val li = ipt_li.text.toString()
        val liRegex = """^(?:(?:http(?:s)?://)?(?:www.)?linkedin\.com/)?([-a-zA-Z0-9@:%_+.~#?&/=]*).*$""".toRegex(RegexOption.MULTILINE)
        val liResult = liRegex.find(li)
        val (liId) = liResult!!.destructured

        val isTipo1 = tipo == resources.getStringArray(R.array.account_types)[0]
        val isTipo2 = tipo == resources.getStringArray(R.array.account_types)[1]

        {
            TODO("Aprimorar a validação do formulário")
        }
        if (nome.isBlank() || (isTipo1 && (cpf.isBlank() || age.isBlank())) || (isTipo2 && cnpj.isBlank()) || adress.isBlank() || phone.isBlank()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val authUser = mAuth.currentUser


                        val bdUser = HashMap<String, Any>()
                        bdUser.put("tipo", tipo)
                        bdUser.put("nome", nome)
                        bdUser.put("email", email)
                        if (isTipo1) {
                            bdUser.put("cpf", cpf)
                            bdUser.put("age", age)
                        } else if (isTipo2) {
                            bdUser.put("cnpj", cnpj)
                        }
                        bdUser.put("adress", adress)
                        bdUser.put("phone", phone)
                        bdUser.put("fb", "https://www.facebook.com/$fbId")
                        bdUser.put("tt", "https://twitter.com/$ttId")
                        bdUser.put("li", "https://www.linkedin.com/$liId")

                        saveDataOnDB(authUser, bdUser)

                        uploadProfilePicture(authUser)
                    } else {
                        {
                            TODO("Refinar mensagem de erro.")
                        }
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()

                    }

                    // ...
                }
    }

    private fun saveDataOnDB(authUser: FirebaseUser?, bdUser: HashMap<String, Any>) {
        db.collection("users")
                .document(authUser?.uid!!)
                .set(bdUser)
                .addOnSuccessListener {
                    startMainActivity()
                }.addOnFailureListener { exception ->
            {
                TODO("Refinar mensagem de erro.")
            }
            Toast.makeText(this, "Não foi possível registrar o usuário. $exception", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadProfilePicture(authUser: FirebaseUser?) {
        val storageRef = storage.reference
        val file: Uri = Uri.fromFile(File(actualPhotoPath))
        val imageRef = storageRef.child("profile_pictures/${authUser?.uid}")
        val uploadTask = imageRef.putFile(file)
        uploadTask.addOnFailureListener { exception ->
            Toast.makeText(this, "Não foi possível fazer upload da imagem. $exception", Toast.LENGTH_SHORT).show()
        }
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw Throwable("Não é possível recuperar a url")
            }

            // Continue with the task to get the download URL
            return@continueWithTask imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                throw Throwable("Não é possível recuperar a url")
                // Handle failures
                // ...
            }

            val downloadUri = task.result
            db.collection("users")
                .document(authUser?.uid!!)
                .update("picture", downloadUri.toString())
                    .addOnFailureListener {
                        Toast.makeText(this, "Não foi possível atualizar o banco com a imagem atual", Toast.LENGTH_SHORT).show()
                    }
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

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            startMainActivity()
        }
    }

    private fun capturarFoto() {
        val intentPhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (intentPhoto.resolveActivity(packageManager) != null) {
            val photoFile = montaArquivo()
            val photoUri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", photoFile)
            intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(intentPhoto, REQUEST_PHOTO)
        } else
            Toast.makeText(this, "Não é possível tirar uma foto.", Toast.LENGTH_SHORT).show()
    }

    private fun montaArquivo(): File {
        val fileName = "${System.currentTimeMillis()}"
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File.createTempFile(fileName, ".png", dir)

        actualPhotoPath = photoFile.absolutePath

        return photoFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Toast.makeText(this, "Image recebida $resultCode $REQUEST_PHOTO ${Activity.RESULT_OK}", Toast.LENGTH_LONG).show()
        if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
            val photoFile = File(actualPhotoPath)

            var bitmap = if (photoFile.exists()) {
                BitmapFactory.decodeFile(photoFile.absolutePath)
            } else {
                data?.extras?.get("data") as Bitmap
            }
            bitmap = verificaRotacao(bitmap)
            first_user_photo.setImageBitmap(bitmap)
            first_user_photo.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun verificaRotacao(bitmap: Bitmap?): Bitmap? {
        val ei = ExifInterface(actualPhotoPath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 ->
                rotateImage(bitmap, 90F)

            ExifInterface.ORIENTATION_ROTATE_180 ->
                rotateImage(bitmap, 180F)

            ExifInterface.ORIENTATION_ROTATE_270 ->
                rotateImage(bitmap, 270F)

            else ->
                bitmap

        }
    }

    private fun rotateImage(source: Bitmap?, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source!!.width, source.height,
                matrix, true)
    }
}

@SuppressLint("MissingPermission")
fun obtemLocalizacao(providerClient: FusedLocationProviderClient, callback: (Double, Double) -> Unit) {
    providerClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            val latitude =  location?.latitude
            val longitude = location?.longitude

            if (latitude !== null && longitude !== null) {
                callback(latitude, longitude)
            }
        }.addOnFailureListener { e ->
            Log.e("Location", "$e")
        }
}