
package your.package.name
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import your.package.name.ui.theme.YourAppNameTheme // Assuming your theme is here
import kotlin.math.roundToInt

// MainActivity is the main entry point for the user interface using Jetpack Compose
class MainActivity : ComponentActivity() {

    // onCreate is called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content of the activity using Jetpack Compose
        setContent {
            // Apply the app theme
            YourAppNameTheme {
                // Call the composable function to build the UI
                AppScreen()
            }
        }
    }
}

// Composable function to define the main screen layout
@OptIn(ExperimentalMaterial3Api::class) // Required for OutlinedTextField
@Composable
fun AppScreen() {
    // State variable to hold the text input in the text field
    var inputText by remember { mutableStateOf("") }
    // State variable to control the visibility of the scientific functions rectangle
    var isRectFVisible by remember { mutableStateOf(false) }

    // Use a Column to arrange elements vertically
    Column(
        modifier = Modifier
            .fillMaxSize() // Fill the entire screen
            .background(Color.White) // Set the overall background to white
    ) {
        // Header Row (takes up only the space it needs)
        Row(
            modifier = Modifier
                .fillMaxWidth() // Fill the width of the parent (Column)
                .background(Color(0xFFFFA500)) // Set background to orange
                .padding(16.dp), // Add padding around the content
            horizontalArrangement = Arrangement.Center, // Center content horizontally
            verticalAlignment = Alignment.CenterVertically // Center content vertically
        ) {
            // Text for the title "Vcalc"
            Text(
                text = "Vcalc",
                color = Color.Black, // Set text color to black
                fontSize = 24.sp, // Example text size
                fontWeight = FontWeight.Bold // Make text bold
            )
        }

        // Text Box (takes up 1/3 of the remaining space)
        OutlinedTextField(
            value = inputText, // The current text value
            onValueChange = { inputText = it }, // Update the state when text changes
            modifier = Modifier
                .fillMaxWidth() // Fill the width of the parent (Column)
                .weight(1f) // Take up 1 unit of weight in the Column (relative to 3 total units below)
                .padding(horizontal = 8.dp, vertical = 8.dp) // Light horizontal padding, padding below header
                .background(Color(0xFFFFA500).copy(alpha = 0.8f)), // Orange background with 80% opacity
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), // Set text color to black
            colors = TextFieldDefaults.outlinedTextFieldColors( // Customize colors for OutlinedTextField
                focusedBorderColor = Color.Transparent, // Remove border when focused
                unfocusedBorderColor = Color.Transparent, // Remove border when unfocused
                cursorColor = Color.Black // Set cursor color
            ),
            // OutlinedTextField is scrollable by default when content exceeds bounds
        )

        // Area for the button and retractable rectangle (takes up the remaining 2/3 of the screen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f) // Take up the remaining 2 units of weight
                .padding(16.dp) // Add some padding around this area
        ) {
            // ButtonF (Round Orange Button)
            Button(
                onClick = { isRectFVisible = true }, // Show rectF when clicked
                modifier = Modifier
                    .align(Alignment.CenterStart) // Align to the left center of the Box
                    .size(48.dp), // Small size for the button
                shape = CircleShape, // Make it round
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)) // Orange background
            ) {
                // You can add an icon or text inside the button if needed
                Text(text = "+", color = Color.Black, fontSize = 20.sp) // Example text
            }

            // RectF (Retractable Scientific Functions Rectangle)
            // Use AnimatedVisibility for slide in/out animation
            androidx.compose.animation.AnimatedVisibility(
                visible = isRectFVisible, // Control visibility with the state
                enter = slideInHorizontally(animationSpec = tween(durationMillis = 300), initialOffsetX = { fullWidth -> fullWidth }), // Slide in from right
                exit = slideOutHorizontally(animationSpec = tween(durationMillis = 300), targetOffsetX = { fullWidth -> fullWidth }) // Slide out to right
            ) {
                // State to track horizontal drag for swipe
                val offsetX = remember { mutableFloatStateOf(0f) }
                val density = LocalDensity.current.density
                val swipeThresholdPx = 100.dp.value * density // Swipe threshold in pixels

                Column(
                    modifier = Modifier
                        .fillMaxHeight() // Fill the height of the parent Box
                        .width(IntrinsicSize.Min) // Shrink width to fit content
                        .align(Alignment.CenterStart) // Align to the left center, next to the button
                        .offset { IntOffset(offsetX.floatValue.roundToInt(), 0) } // Apply drag offset
                        .draggable( // Add draggable modifier for swipe gesture
                            state = rememberDraggableState { delta ->
                                offsetX.floatValue += delta // Update offset during drag
                            },
                            orientation = Orientation.Horizontal, // Allow horizontal drag
                            onDragStopped = { velocity ->
                                // Check if swiped far enough to the right to dismiss
                                if (offsetX.floatValue > swipeThresholdPx) {
                                    isRectFVisible = false // Hide rectF
                                    offsetX.floatValue = 0f // Reset offset
                                } else {
                                    offsetX.floatValue = 0f // Snap back if not swiped far enough
                                }
                            }
                        )
                        .background(Color.Black) // Black background
                        .padding(8.dp), // Padding inside the rectangle
                    horizontalAlignment = Alignment.CenterHorizontally, // Center buttons horizontally
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Add space between buttons
                ) {
                    // Buttons for scientific functions
                    ScientificFunctionButton("sin", inputText) { newInput -> inputText = newInput }
                    ScientificFunctionButton("cos", inputText) { newInput -> inputText = newInput }
                    ScientificFunctionButton("tan", inputText) { newInput -> inputText = newInput }
                    ScientificFunctionButton("ln", inputText) { newInput -> inputText = newInput }
                    ScientificFunctionButton("e", inputText) { newInput -> inputText = newInput }
                    ScientificFunctionButton("sinh", inputText) { newInput -> inputText = newInput }
                    ScientificFunctionButton("cosh", inputText) { newInput -> inputText = newInput }
                    ScientificFunctionButton("tanh", inputText) { newInput -> newInput }
                }
            }
        }
    }
}

// Composable for a single scientific function button
@Composable
fun ScientificFunctionButton(
    symbol: String,
    currentInput: String,
    onInputChange: (String) -> Unit
) {
    Button(
        onClick = {
            // Append the symbol and an opening parenthesis to the input text
            onInputChange(currentInput + symbol + "(")
        },
        modifier = Modifier.size(40.dp), // Small circular button size
        shape = CircleShape, // Make it round
        colors = ButtonDefaults.buttonColors(containerColor = Color.White), // White background
        contentPadding = PaddingValues(0.dp) // Remove default padding
    ) {
        Text(text = symbol, color = Color.Black, fontSize = 12.sp) // Black text
    }
}


// Preview function to see how the UI looks in the design tab
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YourAppNameTheme {
        AppScreen()
    }
}
