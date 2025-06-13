package com.tommy.siliconflow.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import platform.Foundation.NSURL
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerImageURL
import platform.UIKit.UIImagePickerControllerMediaType
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

actual class ImagePicker(
    private val viewController: UIViewController,
    private val onResult: (ImageData) -> Unit,
) {
    actual fun launchPicker() {
        val picker = UIImagePickerController().apply {
            sourceType =
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol,
                UINavigationControllerDelegateProtocol {
                override fun imagePickerController(
                    picker: UIImagePickerController,
                    didFinishPickingMediaWithInfo: Map<Any?, *>
                ) {
                    val imageUrl =
                        didFinishPickingMediaWithInfo[UIImagePickerControllerImageURL] as? NSURL
                    val mimeType =
                        didFinishPickingMediaWithInfo[UIImagePickerControllerMediaType] as? String

                    imageUrl?.let {
                        onResult.invoke(
                            ImageData(
                                uri = it.absoluteString ?: "",
                                mimeType = mimeType
                            )
                        )
                    }
                    picker.dismissViewControllerAnimated(true, null)
                }

                override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                    picker.dismissViewControllerAnimated(true, null)
                }
            }
        }
        viewController.presentViewController(picker, true, null)
    }
}

@Composable
actual fun rememberImagerPicker(onResult: (ImageData) -> Unit): ImagePicker {
    val viewController = LocalUIViewController.current
    val imagePicker = remember { ImagePicker(viewController, onResult) }
    return imagePicker
}
