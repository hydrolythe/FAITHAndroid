package be.hogent.faith.faith.util

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

const val URL_FEEDBACK_FORM_MENTOR =
    "https://forms.office.com/Pages/ResponsePage.aspx?id=DjH3XBoJxUus1ybHIdTMzRPFNmeb7opOh6T2LKWgunRUQUtPRDU0WEFBM0VNR0wyTTlJN0UyQUo0RiQlQCN0PWcu"
const val URL_FEEDBACK_FORM_KID =
    "https://forms.office.com/Pages/ResponsePage.aspx?id=DjH3XBoJxUus1ybHIdTMzRPFNmeb7opOh6T2LKWgunRUQlE2QjdDMjQ0S0pLUjdITjFTRzFPTlg3RSQlQCN0PWcu"
const val URL_BUG_REPORT_FORM =
    "https://forms.office.com/Pages/ResponsePage.aspx?id=DjH3XBoJxUus1ybHIdTMzRPFNmeb7opOh6T2LKWgunRURVRVRFpMNUNPTVJZODlNNkIwTDBFUTBSQiQlQCN0PWcu"
const val URL_REGISTER = "https://lifecity.be"

class FeedbackHelper {

    companion object {

        fun openFeedbackFormForMentor(context: Context) {
            openUrl(context, URL_FEEDBACK_FORM_MENTOR)
        }

        fun openFeedbackFormForKid(context: Context) {
            openUrl(context, URL_FEEDBACK_FORM_KID)
        }

        fun openBugForm(context: Context) {
            openUrl(context, URL_BUG_REPORT_FORM)
        }

        fun openRegisterUrl(context: Context) {
            val builder = CustomTabsIntent.Builder()
            val colorInt: Int = Color.parseColor("#00A898")
            builder.setToolbarColor(colorInt)
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(URL_REGISTER))
        }

        private fun openUrl(context: Context, url: String) {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
        }
    }
}
