package com.tommy.siliconflow.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.compose.LocalPlatformContext
import platform.Foundation.NSURL
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerMediaType
import platform.UIKit.UIImagePickerControllerMediaURL
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

actual class ImagePicker(
    private val viewController: UIViewController,
    private val picker: UIImagePickerController,
) {
    actual fun launchPicker() {
        viewController.presentViewController(picker, true, null)
    }
}

@Composable
actual fun rememberImagerPicker(onResult: (ImageData) -> Unit): ImagePicker {
    val viewController = LocalPlatformContext.current as UIViewController
    val picker = UIImagePickerController()
    picker.sourceType =
        UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
    picker.delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol,
        UINavigationControllerDelegateProtocol {
        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>
        ) {
            val imageUrl = didFinishPickingMediaWithInfo[UIImagePickerControllerMediaURL] as? NSURL
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

    val imagePicker = remember { ImagePicker(viewController, picker) }
    return imagePicker
}
