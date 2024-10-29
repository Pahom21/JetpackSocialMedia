package com.example.socialmediasampleapplication

import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalContext
import com.example.socialmediasampleapplication.ui.theme.SocialMediaSampleApplicationTheme
import com.google.android.exoplayer2.ExoPlayer
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SocialMediaSampleApplicationTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    TikTok()
                }
            }
        }
    }
}

data class BottomNavItem(val icon: ImageVector, val label:String)
@Composable
fun BottomNavBar(navController: NavController){
    val items = listOf(
        BottomNavItem(Icons.Default.Home, "Home"),
        BottomNavItem(Icons.Default.Person, "Profile")
    )
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = false,
                onClick = {
                   // Logic to handle a clicking
                }
            )
        }
    }
}

data class Video(
    val id: String,
    val title: String,
    val videoUrl: String, // URL of the video
    val thumbnailUrl: String // URL of the thumbnail
)

// Sample video data
val sampleVideos = listOf(
    Video("1", "Dance Moves", "android.resource://com.example.socialmediasampleapplication/${R.raw.dancing_moves}", "https://via.placeholder.com/150/0000FF/808080?Text=Video1"),
    Video("2", "Sunset Views", "android.resource://com.example.socialmediasampleapplication/${R.raw.sunset_views}", "https://via.placeholder.com/150/FF0000/FFFFFF?Text=Video2"),
    Video("3", "Cooking Recipe", "android.resource://com.example.socialmediasampleapplication/${R.raw.cooking_recipe}", "https://via.placeholder.com/150/00FF00/000000?Text=Video3")
)

@Composable
fun VideoFeedScreen(videos: List<Video>, modifier: Modifier=Modifier){
    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .padding(top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)){
        items(videos){
            video -> VideoItem(video = video)
        }
    }
}

@Composable
fun VideoItem(video: Video) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(video.videoUrl))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true // Start playback automatically
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(700.dp)
            .padding(0.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Video Player
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false // enable controls
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Overlay UI elements
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Section (user info and, dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = rememberImagePainter(video.thumbnailUrl),
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Gray, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = video.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "@elvismakara",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Right-Aligned Buttons (Like, Comment, Share)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .align(Alignment.End)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconWithText(Icons.Default.Favorite, "100", "Like")
                    Spacer(modifier = Modifier.height(16.dp))
                    IconWithText(Icons.Default.Person, "50", "Comment")
                    Spacer(modifier = Modifier.height(16.dp))
                    IconWithText(Icons.Default.Share, "20", "Share")
                }
            }
        }
    }
}

@Composable
fun IconWithText(icon: ImageVector, count: String, contentDescription: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = count,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TikTok(){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        // Provide padding to the content
        VideoFeedScreen(videos = sampleVideos,
            modifier = Modifier.padding(paddingValues)) // Pass in the sample videos
    }
}