import kotlinx.serialization.Serializable

@Serializable
data class gameMode(
    val Target: String,
    val Category: String,
    val Details: String,
    val Timestamp: String,

)




fun HandleJSONParsing(Category: String){

    val keywordText: String = Category

    when(Category){
        "The Dictator" -> {

        }
        "Would I Lie To You" -> {

        }

        "Two Truths & A Lie" -> {

        }
    }
}