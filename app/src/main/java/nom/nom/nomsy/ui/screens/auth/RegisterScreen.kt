package nom.nom.nomsy.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nom.nom.nomsy.ui.theme.NomsyColors
import nom.nom.nomsy.viewModels.AuthViewModel
import nom.nom.nomsy.viewModels.IAuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: IAuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NomsyColors.Background)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "NOMSY",
                color = NomsyColors.Title,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(60.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = NomsyColors.Texts) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = NomsyColors.Title
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.testTag("emailField").fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = NomsyColors.Texts) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Username",
                        tint = NomsyColors.Title
                    )
                },
                modifier = Modifier.testTag("usernameField").fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = NomsyColors.Texts) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = NomsyColors.Title
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = NomsyColors.Title,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                },
                modifier = Modifier.testTag("passwordField").fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", color = NomsyColors.Texts) },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirm Password",
                        tint = NomsyColors.Title
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                        tint = NomsyColors.Title,
                        modifier = Modifier.clickable { confirmPasswordVisible = !confirmPasswordVisible }
                    )
                },
                modifier = Modifier.testTag("confirmPasswordField").fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // Validate inputs before proceeding
                    when {
                        email.isEmpty() -> {
                            Toast.makeText(context, "Please enter an email", Toast.LENGTH_SHORT).show()
                        }
                        !(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) -> {
                            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        }
                        username.isEmpty() -> {
                            Toast.makeText(context, "Please enter a username", Toast.LENGTH_SHORT).show()
                        }
                        password.isEmpty() -> {
                            Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
                        }
                        password.length < 6 -> {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        }
                        password != confirmPassword -> {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // Store credentials in view model and proceed to onboarding
                            authViewModel.setCredentials(username, password, email)
                            navController.navigate("onboarding_welcome")
                        }
                    }
                },
                modifier = Modifier
                    .testTag("nextButton")
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = NomsyColors.Title,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(style = SpanStyle(
                        color = NomsyColors.Title,
                        textDecoration = TextDecoration.Underline
                    )
                    ) {
                        append("Sign in!")
                    }
                },
                color = NomsyColors.Texts,
                modifier = Modifier.testTag("signInText").clickable { navController.navigate("login") }
            )
        }
    }
}

