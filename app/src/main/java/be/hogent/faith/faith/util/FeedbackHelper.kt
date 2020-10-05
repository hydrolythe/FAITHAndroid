package be.hogent.faith.faith.util

import android.content.Context
import android.content.Intent
import android.net.Uri

const val URL_FEEDBACK_FORM_MENTOR =
    "https://forms.office.com/Pages/ResponsePage.aspx?id=DjH3XBoJxUus1ybHIdTMzRPFNmeb7opOh6T2LKWgunRUQUtPRDU0WEFBM0VNR0wyTTlJN0UyQUo0RiQlQCN0PWcu"
const val URL_FEEDBACK_FORM_KID =
    "https://forms.office.com/Pages/ResponsePage.aspx?id=DjH3XBoJxUus1ybHIdTMzRPFNmeb7opOh6T2LKWgunRUQlE2QjdDMjQ0S0pLUjdITjFTRzFPTlg3RSQlQCN0PWcu"

class FeedbackHelper {

    companion object {

        fun openFeedbackFormForMentor(context: Context) {
            openLink(context, URL_FEEDBACK_FORM_MENTOR)
        }

        fun openFeedbackFormForKid(context: Context) {
            openLink(context, URL_FEEDBACK_FORM_KID)
        }

        private fun openLink(context: Context, link: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(intent)
        }
    }
}
