package com.example.ebookstore.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

// ─────────────────────────────────────────────────────────────────────────────
// Stateful entry point
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Register screen — stateful entry point connected to [AuthViewModel].
 *
 * @param onNavigateToLogin  Navigate back to the Login screen.
 * @param onRegisterSuccess  Called once registration + auto-login completes.
 * @param authViewModel      NavGraph-scoped [AuthViewModel]; passed as parameter.
 */
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onRegisterSuccess()
    }

    RegisterScreenContent(
        isLoading         = uiState.isLoading,
        error             = uiState.error,
        onRegister        = { name, email, password ->
            authViewModel.register(name, email, password)
        },
        onNavigateToLogin = onNavigateToLogin,
        onNavigateBack    = onNavigateBack,
        onErrorDismissed  = authViewModel::clearError,
        modifier          = modifier,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content composable
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Stateless register form — safe to use in Compose Previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    isLoading: Boolean,
    error: String?,
    onRegister: (name: String, email: String, password: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onErrorDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    fun validateAndSubmit() {
        nameError = if (name.isBlank()) "Name is required"
        else if (name.trim().length < 2) "Name must be at least 2 characters"
        else null

        emailError = if (email.isBlank()) "Email is required"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) "Enter a valid email"
        else null

        passwordError = if (password.isBlank()) "Password is required"
        else if (password.length < 6) "Password must be at least 6 characters"
        else null

        confirmPasswordError = if (confirmPassword.isBlank()) "Please confirm your password"
        else if (confirmPassword != password) "Passwords do not match"
        else null

        if (nameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
            focusManager.clearFocus()
            onRegister(name.trim(), email.trim(), password)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
            // ── Top app bar with back arrow ────────────────────────────────
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = EBookStoreSpacing.Medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {

        // ── Title ──────────────────────────────────────────────────────────
        Text(
            text  = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(EBookStoreSpacing.XSmall))
        Text(
            text  = "Join us and start reading",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.height(EBookStoreSpacing.XLarge))

        // ── Full name field ────────────────────────────────────────────────
        OutlinedTextField(
            value         = name,
            onValueChange = {
                name = it
                if (nameError != null) nameError = null
                if (error != null) onErrorDismissed()
            },
            modifier      = Modifier.fillMaxWidth(),
            label         = { Text("Full Name") },
            leadingIcon   = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            isError       = nameError != null,
            supportingText = nameError?.let { msg -> { Text(msg) } },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType   = KeyboardType.Text,
                imeAction      = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
        )

        Spacer(Modifier.height(EBookStoreSpacing.Small))

        // ── Email field ────────────────────────────────────────────────────
        OutlinedTextField(
            value         = email,
            onValueChange = {
                email = it
                if (emailError != null) emailError = null
                if (error != null) onErrorDismissed()
            },
            modifier      = Modifier.fillMaxWidth(),
            label         = { Text("Email") },
            leadingIcon   = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            isError       = emailError != null,
            supportingText = emailError?.let { msg -> { Text(msg) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction    = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
        )

        Spacer(Modifier.height(EBookStoreSpacing.Small))

        // ── Password field ─────────────────────────────────────────────────
        OutlinedTextField(
            value         = password,
            onValueChange = {
                password = it
                if (passwordError != null) passwordError = null
                if (error != null) onErrorDismissed()
            },
            modifier      = Modifier.fillMaxWidth(),
            label         = { Text("Password") },
            leadingIcon   = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon  = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                val desc = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = desc)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            isError       = passwordError != null,
            supportingText = passwordError?.let { msg -> { Text(msg) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
        )

        Spacer(Modifier.height(EBookStoreSpacing.Small))

        // ── Confirm password field ─────────────────────────────────────────
        OutlinedTextField(
            value         = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (confirmPasswordError != null) confirmPasswordError = null
                if (error != null) onErrorDismissed()
            },
            modifier      = Modifier.fillMaxWidth(),
            label         = { Text("Confirm Password") },
            leadingIcon   = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon  = {
                val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                val desc = if (confirmPasswordVisible) "Hide password" else "Show password"
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(icon, contentDescription = desc)
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            isError       = confirmPasswordError != null,
            supportingText = confirmPasswordError?.let { msg -> { Text(msg) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
            singleLine = true,
        )

        // ── Server-side / auth error ───────────────────────────────────────
        if (error != null) {
            Spacer(Modifier.height(EBookStoreSpacing.Small))
            Text(
                text      = error,
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(EBookStoreSpacing.Large))

        // ── Register button ────────────────────────────────────────────────
        Button(
            onClick  = { validateAndSubmit() },
            modifier = Modifier.fillMaxWidth(),
            enabled  = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    color       = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text("Create Account")
            }
        }

        Spacer(Modifier.height(EBookStoreSpacing.Medium))

        // ── Navigate to login ──────────────────────────────────────────────
        TextButton(onClick = onNavigateToLogin) {
            Text(
                text  = "Already have an account? Sign In",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
            } // end inner Column
        } // end outer Column (Surface child)
    } // end Surface
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Register Screen — Light")
@Composable
private fun RegisterScreenLightPreview() {
    EBookStoreTheme {
        RegisterScreenContent(
            isLoading         = false,
            error             = null,
            onRegister        = { _, _, _ -> },
            onNavigateToLogin = {},
            onErrorDismissed  = {},
        )
    }
}

@Preview(name = "Register Screen — Dark")
@Composable
private fun RegisterScreenDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        RegisterScreenContent(
            isLoading         = false,
            error             = "An account with this email already exists.",
            onRegister        = { _, _, _ -> },
            onNavigateToLogin = {},
            onErrorDismissed  = {},
        )
    }
}
