package com.example.lookslike

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.button
import android.app.Activity
import android.app.AlertDialog
import android.graphics.*
import android.support.v4.graphics.drawable.DrawableCompat
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.text.Layout
import android.transition.Visibility
import android.transition.VisibilityPropagation
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Error
import java.util.*
import kotlin.random.Random



class MainActivity : AppCompatActivity() {

    var modeloRenderable : ModelRenderable? = null
    lateinit var sillonRenderable:ModelRenderable
    lateinit var sillon2Renderable:ModelRenderable
    lateinit var tableRenderable:ModelRenderable

    lateinit var arFragment: ArFragment
    var eliminable = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContentView(R.layout.activity_main)

        cargarModelos()
        cargarSpinner()

        arFragment = supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            var anchor = hitResult.createAnchor()
            var anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)
            crearModelo(anchorNode)
        }
    }

    private fun cargarModelos(){
        ModelRenderable.builder().setSource(this, R.raw.sillon2).build()
            .thenAccept { modelRenderable: ModelRenderable? -> sillon2Renderable = modelRenderable!! }
            .exceptionally { trhowable: Throwable? ->
                Toast.makeText(this, "No se puede cargar este modelo", Toast.LENGTH_SHORT).show()
                null
             }
        ModelRenderable.builder().setSource(this, R.raw.sillon).build()
            .thenAccept { modelRenderable: ModelRenderable? -> sillonRenderable = modelRenderable!! }
            .exceptionally { trhowable: Throwable? ->
                Toast.makeText(this, "No se puede cargar este modelo", Toast.LENGTH_SHORT).show()
                null
            }
        ModelRenderable.builder().setSource(this, R.raw.table).build()
            .thenAccept { modelRenderable: ModelRenderable? -> tableRenderable = modelRenderable!! }
            .exceptionally { trhowable: Throwable? ->
                Toast.makeText(this, "No se puede cargar este modelo", Toast.LENGTH_SHORT).show()
                null
            }
    }



    fun cargarSpinner(){
        var listaModelos = arrayOf("Sillon Modelo A", "Sillon Modelo B","Mesa Modelo A")
        spinnerModelos.onItemSelectedListener
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaModelos)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModelos!!.adapter = spinnerAdapter

        spinnerModelos.setPositiveButton("¡LISTO!")
        spinnerModelos.setTitle("Buscar modelo")



    }
    private fun crearModelo(anchorNode: AnchorNode) {

        when (spinnerModelos.selectedItem) {
            "Sillon Modelo A" -> modeloRenderable = sillonRenderable
            "Sillon Modelo B" -> modeloRenderable = sillon2Renderable
            "Mesa Modelo A" -> modeloRenderable = tableRenderable
            else -> Toast.makeText(this, "No se puede cargar este modelo", Toast.LENGTH_SHORT).show()
        }

        val modeloACargar = TransformableNode(arFragment.transformationSystem)
        modeloACargar.setParent(anchorNode)
        modeloACargar.renderable = modeloRenderable
        modeloACargar.select()
        

        modeloACargar.setOnTapListener { hitTestResult, motionEvent ->
            arFragment.onPeekTouch(hitTestResult, motionEvent)
            if (eliminable) {
                if (hitTestResult.node != null) {
                    var hitNode = hitTestResult.node

                    if (hitNode!!.renderable === modeloRenderable) {
                        arFragment.arSceneView.scene.removeChild(hitNode)
                        hitNode.setParent(null)
                        hitNode = null
                    }
                }
            }
        }

    }
    fun eliminable(view: View) {

        var estado = eliminable
        eliminable = (!estado)

        if (eliminable == false) {
            botonEliminar.setBackgroundResource(R.drawable.quitar1)
        } else {
            botonEliminar.setBackgroundResource(R.drawable.quitar2)
        }

    }

    fun mostrarModelos(view: View) {
        if (spinnerModelos.visibility == View.GONE) {
            botonAdd.setBackgroundResource(R.drawable.add2)
            spinnerModelos.visibility = View.VISIBLE
        } else if (spinnerModelos.visibility == View.VISIBLE) {
            botonAdd.setBackgroundResource(R.drawable.add1)
            spinnerModelos.visibility = View.GONE
        } else {
            return
        }

    }

    private fun hideStatusBar(){
        if (Build.VERSION.SDK_INT >= 16) {
            window.setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT)
            window.decorView.systemUiVisibility = 3328
        }else{
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun alertaCaptura(view: View){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Captura de pantalla")
        builder.setMessage("Sentimos informarle que la captura de pantalla aún no ha sido implementada en esta versión")

        builder.setPositiveButton("ACEPTAR") { dialog, which ->
            print("intento de captura")
        }

        builder.show()
    }




    /*fun captura(view: View){
        val bitmap = loadBitmapFromView(findViewById(R.id.contenedorPrincipal), 350, 450)
        guardarImagen(bitmap)
    }

    fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(0, 0, v.layoutParams.width, v.layoutParams.height)
        v.draw(c)
        return b
    }

    fun guardarImagen(bitmap: Bitmap){
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/req_images")
        myDir.mkdirs()
        var n = 10000
        val generator = Random.nextInt(n)
        val fname = "Image-$generator.jpg"
        val file = File(myDir, fname)
        //  Log.i(TAG, "" + file);
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/


    }

