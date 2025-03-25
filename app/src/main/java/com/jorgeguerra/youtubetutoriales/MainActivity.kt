package com.jorgeguerra.youtubetutoriales

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.common.io.Files.append
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jorgeguerra.youtubetutoriales.ui.theme.Verde
import com.jorgeguerra.youtubetutoriales.ui.theme.YoutubeTutorialesTheme
import com.jorgeguerra.youtubetutoriales.ui.theme.miFont

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        analytics = Firebase.analytics
        auth = Firebase.auth
        if (auth.currentUser != null) {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        // Configurar Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Mejor práctica
            .requestEmail()
            .build()

        val currentUser = auth.currentUser

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            var correo by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            // Launcher para Google Sign In
            val googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    // Autenticar con Firebase usando el token de Google
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                    Toast.makeText(
                        baseContext,
                        "Authentication with Google failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            YoutubeTutorialesTheme {

                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(Color.Blue)
                ){
                    Column(
                        modifier = Modifier.align(Alignment.Center)
                            .padding(horizontal = 40.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Text(text = "Registrate", fontSize = 36.sp, color = Verde)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = correo,
                            onValueChange = {correo = it},
                            label = { Text("Correo", fontFamily = miFont)},
                            colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Verde,        // Color del borde cuando tiene foco
                                unfocusedBorderColor = Color.Gray,       // Color del borde cuando no tiene foco
                                errorBorderColor = Color.Red,            // Color del borde cuando hay error
                                disabledBorderColor = Color.LightGray    // Color del borde cuando está deshabilitado
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = {password = it},
                            label = { Text("Password", fontFamily = miFont) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { createUserFirebase(correo, password) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,            // Color de fondo del botón
                                contentColor = Color.Blue // Color del texto/contenido
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Registrarme")
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                    Column(modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(horizontal = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    )
                    {
                        Button(
                            onClick = {
                                val signInIntent = googleSignInClient.signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Blue
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth(0.70f)
                            ) {
                                // Icono de Google
                                Image(
                                    painter = painterResource(id = R.drawable.google_brands_solid), // Asegúrate de tener este recurso
                                    contentDescription = "Google Logo",
                                    modifier = Modifier.size(24.dp)
                                )

                                // Espacio entre el icono y el texto
                                Spacer(modifier = Modifier.width(18.dp))

                                // Texto del botón
                                Text(text = "Registrarse con Google")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { startActivity(Intent(this@MainActivity, LoginActivity::class.java)) }
                        ) {
                            val annotatedString = buildAnnotatedString {
                                append("¿Ya tienes cuenta? Ingresa sesión ")
                                withStyle(
                                    style = SpanStyle(
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                ) {
                                    append("AQUÍ")
                                }
                            }

                            Text(text = annotatedString)
                        }
                    }
                }
            }
        }
    }

    private fun createUserFirebase(correo: String, password: String) {
        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                baseContext,
                "Correo y contraseña son requeridos",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            auth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(
                            baseContext,
                            "Creación exitosa: ${user?.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext,
                        "Inicio sesión con Google exitoso: ${user?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Fallo en inicio de sesión con Google: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}