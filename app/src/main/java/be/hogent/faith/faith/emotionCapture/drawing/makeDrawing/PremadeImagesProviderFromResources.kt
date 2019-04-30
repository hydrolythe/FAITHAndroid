package be.hogent.faith.faith.emotionCapture.drawing.makeDrawing

import be.hogent.faith.R

class PremadeImagesProviderFromResources : PremadeImagesProvider {
    override fun provideImages(): List<Int> {
        return listOf(
            R.drawable.image_auto,
            R.drawable.image_auto,
            R.drawable.image_auto,
            R.drawable.image_mannetje,
            R.drawable.image_auto,
            R.drawable.image_mannetje,
            R.drawable.image_auto,
            R.drawable.image_mannetje,
            R.drawable.image_mannetje,
            R.drawable.image_mannetje,
            R.drawable.image_auto,
            R.drawable.image_mannetje,
            R.drawable.image_auto,
            R.drawable.image_mannetje,
            R.drawable.image_mannetje
        )
    }
}
