package co.dragva.a500walls

class RedditAPI(CLIENT_ID: String) {

    private var clientID : String = ""
    private val REDDIT_URL = "http://"

    init {
        clientID = CLIENT_ID
    }

    fun get(subreddit: String, params: String) {
        
    }

}