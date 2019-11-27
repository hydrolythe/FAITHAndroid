package be.hogent.faith.faith.util

import android.view.View
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


fun hasTextInputLayoutHintText(expectedError : String) : Matcher<View> {
    return object: TypeSafeMatcher<View>(){
        override fun describeTo(description: Description?) {
            description?.appendText("should be error : $expectedError or of type TextInputLayout")
        }

        /**
         * Checks wether the error for the [TextInputLayout] is the same as the
         * expected error.
         */
        override fun matchesSafely(item: View?): Boolean {
            if( !(item is TextInputLayout)) {
                return false
            }
            val error = (item as TextInputLayout).error ?: return false
            val hint = error.toString()
            return expectedError == hint
        }


    }
}