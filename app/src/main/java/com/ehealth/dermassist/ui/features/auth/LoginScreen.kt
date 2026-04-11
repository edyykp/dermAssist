package com.ehealth.dermassist.ui.features.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ehealth.dermassist.ui.components.DermButton
import com.ehealth.dermassist.ui.components.DermTextField
import com.ehealth.dermassist.ui.components.GoogleButton
import com.ehealth.dermassist.ui.components.LoadingOverlay
import com.ehealth.dermassist.ui.theme.dimens
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
) {
    var isSignUp by remember { mutableStateOf(true) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isLoading by authViewModel.isLoading.collectAsState()
    val authError by authViewModel.authError.collectAsState()

    // Derived validation states from ViewModel
    val isEmailValid = authViewModel.isEmailValid(email)
    val isPasswordValid = authViewModel.isPasswordValid(password)
    val isFullNameValid = authViewModel.isFullNameValid(fullName)
    val doPasswordsMatch = authViewModel.doPasswordsMatch(password, confirmPassword)

    LaunchedEffect(authError) {
        authError?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                authViewModel.clearError()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .statusBarsPadding()
                            .padding(
                                horizontal = MaterialTheme.dimens.sm,
                                vertical = MaterialTheme.dimens.md,
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding()
            ) {
                Column(
                    modifier =
                        Modifier.weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = MaterialTheme.dimens.grid3)
                ) {
                    Text(
                        text = if (isSignUp) "Create account" else "Welcome back",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.grid075))

                    Text(
                        text =
                            if (isSignUp) "Join DermAssist to start your skin health journey."
                            else "Sign in to continue your skin health journey.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.grid35))

                    // Animated Tab Toggle
                    val alignment by
                        animateFloatAsState(
                            targetValue = if (isSignUp) -1f else 1f,
                            label = "tabAlignment",
                        )

                    Box(
                        modifier =
                            Modifier.fillMaxWidth()
                                .background(
                                    color = Color(0xFFF3F5F7),
                                    shape = RoundedCornerShape(MaterialTheme.dimens.grid175),
                                )
                                .padding(MaterialTheme.dimens.grid05)
                    ) {
                        Box(modifier = Modifier.matchParentSize()) {
                            Box(
                                modifier =
                                    Modifier.fillMaxWidth(0.5f)
                                        .fillMaxHeight()
                                        .align(BiasAlignment(alignment, 0f))
                                        .shadow(
                                            elevation = MaterialTheme.dimens.xxs,
                                            shape = RoundedCornerShape(MaterialTheme.dimens.grid125),
                                        )
                                        .background(
                                            MaterialTheme.colorScheme.onPrimary,
                                            RoundedCornerShape(MaterialTheme.dimens.grid125),
                                        )
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            TabButton(
                                text = "Sign Up",
                                isSelected = isSignUp,
                                onClick = { isSignUp = true },
                                modifier = Modifier.weight(1f),
                            )
                            TabButton(
                                text = "Log In",
                                isSelected = !isSignUp,
                                onClick = { isSignUp = false },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.grid35))

                    // Form Fields
                    AnimatedVisibility(visible = isSignUp) {
                        DermTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "Full Name",
                            placeholder = "Enter your full name",
                            modifier = Modifier.padding(bottom = MaterialTheme.dimens.grid2),
                            isError = fullName.isNotEmpty() && !isFullNameValid,
                            errorMessage =
                                if (fullName.isNotEmpty() && !isFullNameValid)
                                    "Name cannot be empty"
                                else null,
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                ),
                            keyboardActions =
                                KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                        )
                    }

                    DermTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        placeholder = "Enter your email",
                        modifier = Modifier.padding(bottom = MaterialTheme.dimens.grid2),
                        isError = email.isNotEmpty() && !isEmailValid,
                        errorMessage =
                            if (email.isNotEmpty() && !isEmailValid) "Invalid email format"
                            else null,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                    )

                    DermTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "Enter your password",
                        visualTransformation =
                            if (isPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible },
                                modifier = Modifier.size(MaterialTheme.dimens.md),
                            ) {
                                Icon(
                                    imageVector =
                                        if (isPasswordVisible) Icons.Default.VisibilityOff
                                        else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint =
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.5f
                                        ),
                                )
                            }
                        },
                        modifier =
                            Modifier.padding(
                                bottom =
                                    if (isSignUp) MaterialTheme.dimens.xxs
                                    else MaterialTheme.dimens.grid2
                            ),
                        isError = password.isNotEmpty() && !isPasswordValid,
                        errorMessage =
                            if (password.isNotEmpty() && !isPasswordValid) "Password too short"
                            else null,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (isSignUp) ImeAction.Next else ImeAction.Done,
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = {
                                    focusManager.clearFocus()
                                    if (!isSignUp && isEmailValid && isPasswordValid) {
                                        authViewModel.login(email, password, onSuccess)
                                    }
                                },
                            ),
                    )

                    if (isSignUp) {
                        PasswordStrengthIndicator(password)
                        Spacer(modifier = Modifier.height(MaterialTheme.dimens.grid2))

                        DermTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            placeholder = "Re-enter password",
                            isError = confirmPassword.isNotEmpty() && !doPasswordsMatch,
                            errorMessage =
                                if (confirmPassword.isNotEmpty() && !doPasswordsMatch)
                                    "Passwords do not match"
                                else null,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.padding(bottom = MaterialTheme.dimens.grid2),
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done,
                                ),
                            keyboardActions =
                                KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (
                                            isEmailValid &&
                                                isPasswordValid &&
                                                isFullNameValid &&
                                                doPasswordsMatch
                                        ) {
                                            authViewModel.signUp(
                                                email,
                                                password,
                                                fullName,
                                                onSuccess,
                                            )
                                        }
                                    }
                                ),
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.grid1))

                    DermButton(
                        text = if (isSignUp) "Create Account" else "Log In",
                        onClick = {
                            focusManager.clearFocus()
                            if (isSignUp) {
                                authViewModel.signUp(email, password, fullName, onSuccess)
                            } else {
                                authViewModel.login(email, password, onSuccess)
                            }
                        },
                        variant = com.ehealth.dermassist.ui.components.ButtonVariant.Secondary,
                        enabled =
                            if (isSignUp) {
                                isEmailValid &&
                                    isPasswordValid &&
                                    isFullNameValid &&
                                    doPasswordsMatch
                            } else {
                                isEmailValid && isPasswordValid
                            },
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.grid25))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outline,
                        )
                        Text(
                            text = "or continue with",
                            modifier = Modifier.padding(horizontal = MaterialTheme.dimens.grid15),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.grid15))

                    GoogleButton { authViewModel.handleGoogleSignIn(context, onSuccess) }

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.grid2))

                    Box(
                        modifier =
                            Modifier.fillMaxWidth().padding(bottom = MaterialTheme.dimens.grid4),
                        contentAlignment = Alignment.Center,
                    ) {
                        @Suppress("DEPRECATION")
                        Text(
                            text =
                                buildAnnotatedString {
                                    append(
                                        if (isSignUp) "Already have an account? "
                                        else "Don't have an account? "
                                    )
                                    withStyle(
                                        style =
                                            SpanStyle(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                            )
                                    ) {
                                        append(if (isSignUp) "Log In" else "Sign Up")
                                    }
                                },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable { isSignUp = !isSignUp },
                        )
                    }
                }
            }
        }

        if (isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier.clickable { onClick() }.padding(vertical = MaterialTheme.dimens.grid125),
        contentAlignment = Alignment.Center,
    ) {
        @Suppress("DEPRECATION")
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color =
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    if (password.isEmpty()) return

    val strength =
        when {
            password.length < 6 -> 0.3f
            password.any { !it.isLetterOrDigit() } -> 1f
            else -> 0.6f
        }
    val strengthText =
        when {
            password.length < 6 -> "Weak – too short"
            password.any { !it.isLetterOrDigit() } -> "Strong password"
            else -> "Medium strength – add special characters"
        }
    val color =
        when {
            password.length < 6 -> MaterialTheme.colorScheme.error
            password.any { !it.isLetterOrDigit() } -> MaterialTheme.colorScheme.primary
            else -> Color(0xFFF59E0B) // Amber
        }

    Column(modifier = Modifier.padding(top = MaterialTheme.dimens.grid1)) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(MaterialTheme.dimens.grid05)
                    .background(
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(MaterialTheme.dimens.xxs),
                    )
        ) {
            Box(
                modifier =
                    Modifier.fillMaxWidth(strength)
                        .fillMaxHeight()
                        .background(color, RoundedCornerShape(MaterialTheme.dimens.xxs))
            )
        }
        Text(
            text = strengthText,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(top = MaterialTheme.dimens.grid05),
        )
    }
}
