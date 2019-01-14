package be.hogent.faith.domain

class User(
    val actionPlan: ActionPlan = ActionPlan(),
    val emotionDiary: EmotionDiary = EmotionDiary(),
    val timeline: Timeline = Timeline(),
    val treasureChest: TreasureChest = TreasureChest()
)
