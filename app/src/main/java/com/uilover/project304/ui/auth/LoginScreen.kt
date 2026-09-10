package com.uilover.project304.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uilover.project304.R
import com.uilover.project304.ui.components.GlassPrimaryButton
import com.uilover.project304.ui.components.GlassSurface
import com.uilover.project304.ui.components.MovaLogo
import com.uilover.project304.ui.components.MovaLogoVariant
import com.uilover.project304.ui.components.NatureBackdrop
import com.uilover.project304.ui.theme.AntonFamily
import com.uilover.project304.ui.theme.Cream
import com.uilover.project304.ui.theme.NatureImagery
import com.uilover.project304.ui.theme.QuicksandFamily
import com.uilover.project304.ui.theme.SageGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is AuthUiState.Loading
    val errorMessage = (uiState as? AuthUiState.Error)?.message

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> onLoginSuccess()
            else -> Unit
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SageGreen,
        unfocusedBorderColor = Cream.copy(alpha = 0.35f),
        focusedTextColor = Cream,
        unfocusedTextColor = Cream,
        cursorColor = SageGreen,
        focusedLabelColor = SageGreen,
        unfocusedLabelColor = Cream.copy(alpha = 0.7f),
        focusedLeadingIconColor = SageGreen,
        unfocusedLeadingIconColor = Cream.copy(alpha = 0.7f),
        focusedTrailingIconColor = SageGreen,
        unfocusedTrailingIconColor = Cream.copy(alpha = 0.7f)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        NatureBackdrop(
            imageUrl = NatureImagery.LOGIN_BACKGROUND,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(modifier = Modifier.weight(1f))

            MovaLogo(
                variant = MovaLogoVariant.WORDMARK,
                modifier = Modifier
                    .padding(top = 48.dp)
                    .width(180.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                contentPadding = PaddingValues(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.login_welcome_back),
                        fontFamily = AntonFamily,
                        fontSize = 30.sp,
                        color = Cream,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.login_subtitle),
                        fontFamily = QuicksandFamily,
                        fontSize = 14.sp,
                        color = Cream.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (uiState is AuthUiState.Error) viewModel.resetState()
                        },
                        label = { Text(stringResource(R.string.label_email), fontFamily = QuicksandFamily) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        textStyle = TextStyle(fontFamily = QuicksandFamily),
                        colors = fieldColors
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (uiState is AuthUiState.Error) viewModel.resetState()
                        },
                        label = { Text(stringResource(R.string.label_password), fontFamily = QuicksandFamily) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = stringResource(if (passwordVisible) R.string.hide_password else R.string.show_password)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        textStyle = TextStyle(fontFamily = QuicksandFamily),
                        colors = fieldColors
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = errorMessage,
                            color = Color(0xFFFF8A80),
                            fontFamily = QuicksandFamily,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = SageGreen, modifier = Modifier.size(28.dp))
                    } else {
                        GlassPrimaryButton(
                            text = stringResource(R.string.action_sign_in),
                            onClick = { viewModel.signIn(email, password) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = email.isNotEmpty() && password.isNotEmpty()
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.no_account_prompt),
                            color = Cream.copy(alpha = 0.8f),
                            fontFamily = QuicksandFamily,
                            fontSize = 14.sp
                        )
                        Text(
                            text = stringResource(R.string.action_sign_up),
                            color = SageGreen,
                            fontFamily = QuicksandFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onSignUpClick() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
