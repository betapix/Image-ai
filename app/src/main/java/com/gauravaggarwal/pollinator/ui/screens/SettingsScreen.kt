package com.gauravaggarwal.pollinator.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gauravaggarwal.pollinator.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        // Share App
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message))
                    }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_app)))
                }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = stringResource(id = R.string.share_app)
            )
            Text(
                text = stringResource(id = R.string.share_app),
                color = Color.White,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        // Contact Developer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    val model = Build.MANUFACTURER + " " + Build.MODEL
                    val locationLink = ""
                    val body = "Device: $model\nTime: $time\nLocation: $locationLink\n\nPlease describe your issue here..."

                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email_to)))
                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
                        putExtra(Intent.EXTRA_TEXT, body)
                    }
                    context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.email_chooser)))
                }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_email),
                contentDescription = stringResource(id = R.string.contact_developer)
            )
            Text(
                text = stringResource(id = R.string.contact_developer),
                color = Color.White,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}


