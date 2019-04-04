package be.hogent.faith.faith.enterEventDetails

/**
 * TODO: try to find a way to get the activity to launch correctly
 */
// @RunWith(AndroidJUnit4::class)
// class DetailsAreAddedToBottom {
//    private lateinit var uiDevice: UiDevice
//
//    @Rule
//    @JvmField
//    var activityTestRule = ActivityTestRule(MainActivity::class.java)
//
//    @Before
//    fun setUp() {
//        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//    }
//
//    @Test
//    fun eventDetails_newPhotoIsAddedToBottomList() {
//        val activity = activityTestRule.activity
//        Log.d("FUCK", "$activity test")
//        // go to event details screen
//        onView(withId(R.id.main_second_location)).perform(click())
//        // open camera
//        onView(withId(R.id.btn_event_details_camera)).perform(click())
//        allowPermissionsIfNeeded(uiDevice)
//        // take picture
//        onView(withId(R.id.btn_takePhoto_takePhoto)).perform(click())
//        // Wait for picture to be saved
//        // TODO: replace sleep with IdlingResource implementation
//        // See https://developer.android.com/reference/androidx/test/espresso/idling/CountingIdlingResource.html
//        Thread.sleep(3000)
//        // Enter picture recordingName
//        onView(withId(R.id.txt_save_photo_name)).perform(typeText("Photo recordingName"))
//        // Click Save
//        onView(withId(R.id.btn_save_photo_save)).perform(click())
//        // back to overview
//        pressBack()
//        // Check if thumbnail was added
//        onView(withId(R.id.recyclerView_event_details_details)).check(RecyclerViewItemCountAssertion(1))
//    }
//
//    @After
//    fun tearDown() {
//        activityTestRule.finishActivity()
//    }
// }