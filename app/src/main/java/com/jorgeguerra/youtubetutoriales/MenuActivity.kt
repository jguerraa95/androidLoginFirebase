package com.jorgeguerra.youtubetutoriales

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jorgeguerra.youtubetutoriales.ui.theme.miFont

@Suppress("DEPRECATION")
class MenuActivity : ComponentActivity() {
    //Creación de variables firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Obtenemos el usuario en sesion
        auth = Firebase.auth

        // Configuración de Google Sign In para cerrar sesión
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val currentUser = auth.currentUser
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val photoUrl = account?.photoUrl
        if (currentUser === null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContent {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)
                .padding(25.dp))
            {
                Column(modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(
                        text = "¡Bienvenido!",
                        color = Color.White,
                        fontFamily = miFont,
                        fontSize = 36.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Spacer(Modifier.height(10.dp))
                    if (currentUser != null) {
                        Text(
                            text = "${currentUser.email}",
                            color = Color.White,
                            fontFamily = miFont,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "${currentUser.uid}",
                            color = Color.White,
                            fontFamily = miFont,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Column (
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        if (currentUser != null) {
                            Row (verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement =Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp))
                            {
                                Text(
                                    text = "Nombre : ",
                                    color = Color.White,
                                    fontFamily = miFont,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${currentUser.displayName ?: "No disponible"}",
                                    color = Color.White,
                                    fontFamily = miFont,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Row (verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement =Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp))
                            {
                                Text(
                                    text = "Verificado : ",
                                    color = Color.White,
                                    fontFamily = miFont,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${if (currentUser.isEmailVerified) "Sí" else "No"}",
                                    color = Color.White,
                                    fontFamily = miFont,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Row (verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement =Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp))
                            {
                                Text(
                                    text = "Proveedor : ",
                                    color = Color.White,
                                    fontFamily = miFont,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${currentUser.providerData.firstOrNull()?.providerId ?: "Desconocido"}",
                                    color = Color.White,
                                    fontFamily = miFont,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }

                    Button(
                        onClick = {
                            try {
                                // Cerrar sesión en Firebase
                                auth.signOut()

                                // Cerrar sesión en Google
                                googleSignInClient.signOut().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "Google sign out successful")
                                        Toast.makeText(
                                            baseContext,
                                            "Sesión cerrada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Log.e(TAG, "Google sign out failed", task.exception)
                                    }

                                    // Regresar a la actividad de login
                                    val intent =
                                        Intent(this@MenuActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                            } catch (e: Exception) {
                                Log.e(TAG, "Error during sign out process", e)
                                Toast.makeText(
                                    baseContext,
                                    "Error al cerrar sesión: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text("Cerrar sesión")
                    }

                }
            }
        }
    }

}