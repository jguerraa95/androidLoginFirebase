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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.jorgeguerra.youtubetutoriales.ui.theme.Verde
import com.jorgeguerra.youtubetutoriales.ui.theme.miFont


class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        if (auth.currentUser != null) {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
        // Configurar Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {

            var correo by remember {mutableStateOf("")}
            var password by remember { mutableStateOf("") }


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

            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Blue)
            )
            {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                        .fillMaxWidth().padding(40.dp)
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontFamily = miFont,
                        color = Verde,
                        fontSize = 36.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo", fontFamily = miFont, color = Verde) }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", fontFamily = miFont, color = Verde) },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            loginInFirebase(correo, password)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,            // Color de fondo del botón
                            contentColor = Color.Blue // Color del texto/contenido
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
                    ) {
                        Text(text = "Iniciar Sesión")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                }
                Column(modifier = Modifier.align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Blue
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .fillMaxWidth().height(50.dp)
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
                    Spacer(Modifier.height(5.dp))
                    TextButton(
                        modifier = Modifier.padding(bottom = 10.dp).height(50.dp) ,
                        onClick = { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }
                    ) {
                        val annotatedString = buildAnnotatedString {
                            append("¿Aun no tienes cuenta? Registrate ")
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

    private fun loginInFirebase(correo: String, password: String) {
        if(correo.isEmpty() || password.isEmpty()){
            Toast.makeText(
                baseContext,
                "Ingresa correo y contraseña",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            auth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login exitoso
                        Log.d(TAG, "signInWithEmail:success")
                        startActivity(Intent(this@LoginActivity, MenuActivity::class.java))
                        val user = auth.currentUser
                        Toast.makeText(
                            baseContext,
                            "Login exitoso: ${user?.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        // Aquí puedes navegar a tu actividad principal
                    } else {
                        // Login fallido
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Login fallido: ${task.exception?.message}",
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
                    startActivity(Intent(this@LoginActivity, MenuActivity::class.java))
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext,
                        "Inicio sesión con Google exitoso: ${user?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    // Aquí puedes navegar a tu actividad principal
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