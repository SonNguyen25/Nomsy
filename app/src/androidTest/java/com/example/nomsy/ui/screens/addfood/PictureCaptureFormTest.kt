//package com.example.nomsy.ui.screens.addfood
//
//import androidx.compose.ui.test.*
//import androidx.compose.ui.test.junit4.createComposeRule
//import com.example.nomsy.data.remote.models.FoodDetail
//import com.example.nomsy.viewModels.FoodViewModel
//import org.junit.Rule
//import org.junit.Test
//import androidx.lifecycle.MutableLiveData
//import androidx.compose.runtime.Composable
//import com.example.nomsy.ui.components.PictureCaptureForm
//
//class PictureCaptureFormTest {
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    @Test
//    fun pictureCaptureForm_showsPlaceholder_andCameraButton() {
//        composeTestRule.setContent {
//            PictureCaptureFormTestWrapper()
//        }
//
//        composeTestRule.onNodeWithTag("PlaceholderText").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("CameraButton").assertIsDisplayed()
//    }
//
//    @Test
//    fun pictureCaptureForm_displaysRecognizedFoodAndDetails() {
//        composeTestRule.setContent {
//            PictureCaptureFormTestWrapper(
//                fakeRecognizedFood = "Broccoli",
//                fakeFoodDetail = foodDetail("Broccoli", 55, 11f, 2f, 0.3f)
//            )
//        }
//
//        composeTestRule.onNodeWithTag("DetectedFood").assertTextContains("Broccoli")
//        composeTestRule.onNodeWithTag("FoodDetails").assertIsDisplayed()
//    }
//}
//
//// Fake wrapper with injected fake ViewModel
//@Composable
//fun PictureCaptureFormTestWrapper(
//    fakeRecognizedFood: String = "",
//    fakeFoodDetail: FoodDetail? = null
//) {
//    val fakeViewModel = object : FoodViewModel() {
//        override val recognizedFood = MutableLiveData(fakeRecognizedFood)
//        override val foodDetail = MutableLiveData(fakeFoodDetail)
//    }
//
//    CompositionLocalProvider(LocalViewModelStoreOwner provides object : ViewModelStoreOwner {
//        override fun getViewModelStore() = ViewModelStore()
//    }) {
//        CompositionLocalProvider(LocalContext provides LocalContext.current) {
//            CompositionLocalProvider(
//                LocalViewModelStoreOwner provides object : ViewModelStoreOwner {
//                    override fun getViewModelStore() = ViewModelStore()
//                }
//            ) {
//                CompositionLocalProvider(LocalContext provides LocalContext.current) {
//                    CompositionLocalProvider(LocalViewModelStoreOwner provides object : ViewModelStoreOwner {
//                        override fun getViewModelStore() = ViewModelStore()
//                    }) {
//                        PictureCaptureForm()
//                    }
//                }
//            }
//        }
//    }
//}
