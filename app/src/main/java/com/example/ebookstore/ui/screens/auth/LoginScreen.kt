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
 * Login screen — stateful entry point connected to [AuthViewModel].
 *
 * @param onNavigateToRegister Navigate to the Register screen.
 * @param onLoginSuccess       Called once login completes successfully.
 * @param authViewModel        NavGraph-scoped [AuthViewModel]; passed as parameter.
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    // Navigate away as soon as the user is logged in.
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    LoginScreenContent(
        isLoading            = uiState.isLoading,
        error                = uiState.error,
        onLogin              = { email, password ->
            authViewModel.login(email, password)
        },
        onNavigateToRegister = onNavigateToRegister,
        onNavigateBack       = onNavigateBack,
        onErrorDismissed     = authViewModel::clearError,
        modifier             = modifier,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content composable
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Stateless login form — safe to use in Compose Previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    isLoading: Boolean,
    error: String?,
    onLogin: (email: String, password: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onErrorDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    // Clear transient field errors when the user types.
    fun validateAndSubmit() {
        emailError = if (email.isBlank()) "Email is required"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) "Enter a valid email"
        else null

        passwordError = if (password.isBlank()) "Password is required"
        else if (password.length < 6) "Password must be at least 6 characters"
        else null

        if (emailError == null && passwordError == null) {
            focusManager.clearFocus()
            onLogin(email.trim(), password)
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
            text  = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(EBookStoreSpacing.XSmall))
        Text(
            text  = "Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.height(EBookStoreSpacing.XLarge))

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

        // ── Login button ───────────────────────────────────────────────────
        Button(
            onClick  = { validateAndSubmit() },
            modifier = Modifier.fillMaxWidth(),
            enabled  = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color    = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text("Sign In")
            }
        }

        Spacer(Modifier.height(EBookStoreSpacing.Medium))

        // ── Navigate to register ───────────────────────────────────────────
        TextButton(onClick = onNavigateToRegister) {
            Text(
                text  = "Don't have an account? Sign Up",
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

@Preview(name = "Login Screen — Light")
@Composable
private fun LoginScreenLightPreview() {
    EBookStoreTheme {
        LoginScreenContent(
            isLoading            = false,
            error                = null,
            onLogin              = { _, _ -> },
            onNavigateToRegister = {},
            onErrorDismissed     = {},
        )
    }
}

@Preview(name = "Login Screen — Dark")
@Composable
private fun LoginScreenDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        LoginScreenContent(
            isLoading            = false,
            error                = "Invalid email or password.",
            onLogin              = { _, _ -> },
            onNavigateToRegister = {},
            onErrorDismissed     = {},
        )
    }
}
