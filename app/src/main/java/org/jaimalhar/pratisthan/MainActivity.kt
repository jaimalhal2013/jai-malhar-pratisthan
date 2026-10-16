
package org.jaimalhar.pratisthan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Blue = Color(0xFF07579A)
private val PaleBlue = Color(0xFFEAF4FF)
private val Ink = Color(0xFF172B3A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Blue,
                    secondary = Color(0xFF2B78C5),
                    background = Color.White
                )
            ) {
                AppRoot()
            }
        }
    }
}

data class Donation(
    val id: String,
    val name: String,
    val mobile: String,
    val amount: Double,
    val mode: String,
    val status: String,
    val collector: String,
    val timestamp: String
)

@Composable
private fun AppRoot() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loggedIn by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf("Collector") }
    var screen by remember { mutableStateOf("Dashboard") }
    var morePage by remember { mutableStateOf<String?>(null) }
    var signedInName by remember { mutableStateOf("") }

    val donations = remember { mutableStateListOf<Donation>() }

    if (!loggedIn) {
        LoginScreen(
            username = username,
            onUsername = { username = it },
            password = password,
            onPassword = { password = it },
            onLogin = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    signedInName = username.trim()
                    role = if (username.trim().equals("admin", true)) {
                        "Super Admin"
                    } else {
                        "Collector"
                    }
                    loggedIn = true
                    screen = "Dashboard"
                    morePage = null
                }
            }
        )
    } else {
        Scaffold(
            topBar = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Blue)
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Text(
                        "Jai Malhar Pratisthan",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Donation Collection  •  $role",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    listOf("Dashboard", "Add", "Records", "More")
                        .forEach { item ->
                            NavigationBarItem(
                                selected = screen == item,
                                onClick = {
                                    screen = item
                                    morePage = null
                                },
                                icon = {
                                    Text(
                                        when (item) {
                                            "Dashboard" -> "⌂"
                                            "Add" -> "+"
                                            "Records" -> "▤"
                                            else -> "☰"
                                        },
                                        fontSize = 20.sp
                                    )
                                },
                                label = { Text(item) }
                            )
                        }
                }
            }
        ) { padding ->
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF7FAFD))
            ) {
                when (screen) {
                    "Dashboard" -> DashboardScreen(
                        donations = donations,
                        user = signedInName,
                        onAdd = {
                            screen = "Add"
                            morePage = null
                        },
                        onRecords = {
                            screen = "Records"
                            morePage = null
                        }
                    )

                    "Add" -> AddDonationScreen(signedInName) { donation ->
                        donations.add(0, donation)
                        screen = "Records"
                        morePage = null
                    }

                    "Records" -> RecordsScreen(donations) { old, status ->
                        val index = donations.indexOf(old)
                        if (index >= 0) {
                            donations[index] = old.copy(status = status)
                        }
                    }

                    "More" -> {
                        if (morePage == null) {
                            MoreScreen(
                                role = role,
                                onOpen = { page -> morePage = page },
                                onLogout = {
                                    loggedIn = false
                                    password = ""
                                    screen = "Dashboard"
                                    morePage = null
                                }
                            )
                        } else {
                            MoreDetailScreen(
                                page = morePage!!,
                                username = signedInName,
                                role = role,
                                donations = donations,
                                onBack = { morePage = null }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(
    username: String,
    onUsername: (String) -> Unit,
    password: String,
    onPassword: (String) -> Unit,
    onLogin: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Blue)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("🙏", fontSize = 54.sp, color = Color.White)
        Text(
            "Jai Malhar Pratisthan",
            color = Color.White,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Donation Collection App",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 16.sp
        )

        Spacer(Modifier.height(30.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Login",
                    color = Ink,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = onUsername,
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = onPassword,
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                Text(
                    "Prototype login only. Secure authentication is not connected.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DashboardScreen(
    donations: List<Donation>,
    user: String,
    onAdd: () -> Unit,
    onRecords: () -> Unit
) {
    val received = donations
        .filter { it.status == "Received" }
        .sumOf { it.amount }

    val pending = donations
        .filter { it.status == "Pending" }
        .sumOf { it.amount }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "Welcome, $user",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard(
                    "Received",
                    "₹${money(received)}",
                    Color(0xFFE8F7EE),
                    Modifier.weight(1f)
                )
                SummaryCard(
                    "Pending",
                    "₹${money(pending)}",
                    Color(0xFFFFF3E2),
                    Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard(
                    "Total entries",
                    donations.size.toString(),
                    PaleBlue,
                    Modifier.weight(1f)
                )
                SummaryCard(
                    "Today",
                    donations.count {
                        it.timestamp.startsWith(today())
                    }.toString(),
                    PaleBlue,
                    Modifier.weight(1f)
                )
            }
        }

        item {
            Button(
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("+  New Donation")
            }
        }

        item {
            OutlinedButton(
                onClick = onRecords,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("View Donation Records")
            }
        }

        item {
            Text(
                "Recent activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
        }

        if (donations.isEmpty()) {
            item {
                EmptyState(
                    "No donations yet",
                    "Tap New Donation to record the first collection."
                )
            }
        }

        items(donations.take(5)) { DonationCard(it) }

        item {
            Text(
                "Google Drive and SMS are not connected yet.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                title,
                color = Ink.copy(alpha = 0.75f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                color = Ink,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AddDonationScreen(
    collector: String,
    onSave: (Donation) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("Cash") }
    var status by remember { mutableStateOf("Pending") }
    var error by remember { mutableStateOf("") }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()

            if (!spokenText.isNullOrBlank()) {
                name = spokenText
                error = ""
            }
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "New Donation",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Payer Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(
                        onClick = {
                            val intent = Intent(
                                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                            ).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE,
                                    "mr-IN"
                                )
                                putExtra(
                                    RecognizerIntent.EXTRA_PROMPT,
                                    "देणगीदाराचे नाव बोला"
                                )
                            }

                            try {
                                speechLauncher.launch(intent)
                            } catch (_: Exception) {
                                error = "Speech recognition is unavailable."
                            }
                        }
                    ) {
                        Text("🎙")
                    }
                }
            )
        }

        item {
            Text(
                "Tap the microphone and speak the payer's name.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        item {
            OutlinedTextField(
                value = mobile,
                onValueChange = {
                    mobile = it.filter(Char::isDigit).take(10)
                },
                label = { Text("Mobile Number") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it.filter { c ->
                        c.isDigit() || c == '.'
                    }.take(10)
                },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text("Payment Method", fontWeight = FontWeight.SemiBold)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Cash", "UPI QR").forEach { option ->
                    FilterChip(
                        selected = mode == option,
                        onClick = { mode = option },
                        label = { Text(option) }
                    )
                }
            }
        }

        if (mode == "UPI QR") {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = PaleBlue
                    )
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text("Common UPI QR", fontWeight = FontWeight.Bold)
                        Text(
                            "QR image upload is not connected yet.",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Text("Payment Status", fontWeight = FontWeight.SemiBold)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Pending", "Received").forEach { option ->
                    FilterChip(
                        selected = status == option,
                        onClick = { status = option },
                        label = { Text(option) }
                    )
                }
            }
        }

        if (status == "Received") {
            item {
                Text(
                    "Only mark Received after confirming the payment.",
                    color = Color(0xFF267344),
                    fontSize = 12.sp
                )
            }
        }

        if (error.isNotBlank()) {
            item {
                Text(error, color = Color(0xFF9A5B00))
            }
        }

        item {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()

                    error = when {
                        name.isBlank() ->
                            "Enter payer name."
                        !mobile.matches(Regex("[0-9]{10}")) ->
                            "Enter a valid 10-digit mobile number."
                        amt == null || !amt.isFinite() || amt <= 0 ->
                            "Enter a valid amount."
                        else -> ""
                    }

                    if (error.isBlank()) {
                        onSave(
                            Donation(
                                id = "JMP-${System.currentTimeMillis()}",
                                name = name.trim(),
                                mobile = mobile,
                                amount = amt!!,
                                mode = mode,
                                status = status,
                                collector = collector,
                                timestamp = SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss",
                                    Locale.US
                                ).format(Date())
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Save Donation")
            }
        }

        item {
            Text(
                "Records are currently stored in app memory only.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun RecordsScreen(
    donations: List<Donation>,
    onUpdate: (Donation, String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }

    val shown = donations.filter {
        (filter == "All" || it.status == filter) &&
            (it.name.contains(query, true) ||
                it.mobile.contains(query))
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Donation Records",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search by name or mobile") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            listOf("All", "Received", "Pending").forEach {
                FilterChip(
                    selected = filter == it,
                    onClick = { filter = it },
                    label = { Text(it) }
                )
            }
        }

        if (shown.isEmpty()) {
            EmptyState(
                "No matching records",
                "New donations will appear here."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shown) { donation ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    donation.name,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    color = Ink
                                )
                                StatusPill(donation.status)
                            }

                            Text(
                                "${donation.mobile}  •  ₹${money(donation.amount)}",
                                color = Ink
                            )

                            Text(
                                "${donation.mode}  •  ${donation.timestamp}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                            Text(
                                "Receipt: ${donation.id}  •  By: ${donation.collector}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                            if (donation.status == "Pending") {
                                TextButton(
                                    onClick = {
                                        onUpdate(donation, "Received")
                                    }
                                ) {
                                    Text("Confirm as Received")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DonationCard(d: Donation) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    d.name,
                    fontWeight = FontWeight.SemiBold,
                    color = Ink
                )
                Text(
                    "${d.mode} • ${d.timestamp}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${money(d.amount)}",
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                StatusPill(d.status)
            }
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    val bg = if (status == "Received") {
        Color(0xFFDDF5E5)
    } else {
        Color(0xFFFFE9C7)
    }

    val fg = if (status == "Received") {
        Color(0xFF17663A)
    } else {
        Color(0xFF8B5200)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            status,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 4.dp
            )
        )
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("▤", fontSize = 34.sp, color = Blue)
        Text(title, fontWeight = FontWeight.Bold, color = Ink)
        Text(subtitle, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun MoreScreen(
    role: String,
    onOpen: (String) -> Unit,
    onLogout: () -> Unit
) {
    val options = listOf(
        "Profile",
        "Change Password",
        "Common UPI QR",
        "SMS Settings",
        "Google Drive",
        "Reports",
        "About App"
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        Text(
            "More",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(options) { label ->
                Card(
                    onClick = { onOpen(label) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            label,
                            modifier = Modifier.weight(1f),
                            color = Ink,
                            fontSize = 16.sp
                        )
                        Text("›", color = Blue, fontSize = 26.sp)
                    }
                }
            }
        }

        if (role == "Super Admin") {
            Text(
                "Admin features need secure backend integration.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

@Composable
private fun MoreDetailScreen(
    page: String,
    username: String,
    role: String,
    donations: List<Donation>,
    onBack: () -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var smsEnabled by remember { mutableStateOf(false) }

    val received = donations
        .filter { it.status == "Received" }
        .sumOf { it.amount }

    val pending = donations
        .filter { it.status == "Pending" }
        .sumOf { it.amount }

    Column(
        Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("‹ Back to More")
        }

        Text(
            page,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )

        Spacer(Modifier.height(18.dp))

        when (page) {
            "Profile" -> {
                Text("Username: $username", color = Ink)
                Spacer(Modifier.height(10.dp))
                Text("Role: $role", color = Ink)
                Spacer(Modifier.height(10.dp))
                Text(
                    "Profile details are currently available for this session only.",
                    color = Color.Gray
                )
            }

            "Change Password" -> {
                Text(
                    "Secure authentication must be connected before a password can be changed.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        message = when {
                            oldPassword.isBlank() ->
                                "Enter your current password."
                            newPassword.length < 8 ->
                                "New password must have at least 8 characters."
                            newPassword != confirmPassword ->
                                "New passwords do not match."
                            else ->
                                "Form validated. Password was NOT changed. Connect secure authentication first."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Validate Password Change")
                }

                if (message.isNotBlank()) {
                    Text(
                        message,
                        modifier = Modifier.padding(top = 12.dp),
                        color = Blue
                    )
                }
            }

            "Common UPI QR" -> {
                Text(
                    "Common UPI QR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "The common QR image is not connected yet. QR image selection and secure storage must be implemented before an admin can upload a QR for all collectors.",
                    color = Color.Gray
                )
            }

            "SMS Settings" -> {
                Text(
                    "Marathi donation thank-you SMS",
                    fontWeight = FontWeight.SemiBold,
                    color = Ink
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "SMS service and sending permissions are not connected yet.",
                    color = Color.Gray
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "SMS preference (local only)",
                        modifier = Modifier.weight(1f),
                        color = Ink
                    )

                    Switch(
                        checked = smsEnabled,
                        onCheckedChange = { smsEnabled = it }
                    )
                }

                Text(
                    if (smsEnabled) {
                        "Preference enabled. No SMS is being sent."
                    } else {
                        "Preference disabled. No SMS is being sent."
                    },
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            "Google Drive" -> {
                Text(
                    "Google Drive: Not connected",
                    fontWeight = FontWeight.SemiBold,
                    color = Ink
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Records currently stay in app memory. Secure Apps Script and Google Sheets integration is required for persistent storage and synchronization.",
                    color = Color.Gray
                )
            }

            "Reports" -> {
                Text(
                    "Donation Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Ink
                )

                Spacer(Modifier.height(12.dp))

                SummaryCard(
                    "Total Entries",
                    donations.size.toString(),
                    PaleBlue,
                    Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                SummaryCard(
                    "Received Amount",
                    "₹${money(received)}",
                    Color(0xFFE8F7EE),
                    Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                SummaryCard(
                    "Pending Amount",
                    "₹${money(pending)}",
                    Color(0xFFFFF3E2),
                    Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Reports show records currently in app memory only.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            "About App" -> {
                Text(
                    "Jai Malhar Pratisthan",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )

                Spacer(Modifier.height(8.dp))

                Text("Donation Collection App")
                Text("Version 0.1.0")
                Text("Payment modes: Cash and UPI QR")
                Text("Status tracking: Pending and Received")

                Spacer(Modifier.height(12.dp))

                Text(
                    "Secure authentication, Google Drive synchronization, QR management and SMS delivery still require integration.",
                    color = Color.Gray
                )
            }

            else -> {
                Text("Page not found.", color = Color.Gray)
            }
        }
    }
}

private fun money(value: Double): String =
    String.format(Locale.US, "%,.2f", value)

private fun today(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
