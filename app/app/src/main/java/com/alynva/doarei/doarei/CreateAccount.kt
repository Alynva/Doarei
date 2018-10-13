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
import kotlinx.android.synthetic.main.activity_create_account.*
import java.io.File

class CreateAccount : AppCompatActivity() {

    companion object {
        const val REQUEST_PHOTO = 200
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

        val adapter = ArrayAdapter.createFromResource(this, R.array.account_types, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_account_type.setAdapter(adapter)
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
