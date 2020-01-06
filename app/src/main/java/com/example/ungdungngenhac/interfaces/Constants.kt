package com.example.ungdungngenhac.interfaces

class Constants {
    interface ACTION{
        companion object{
            val ACTION_PREVIOUS="com.example.ungdungngenhac.action.previous"
            val ACTION_NEXT="com.example.ungdungngenhac.action.next"
            val ACTION_PLAY="com.example.ungdungngenhac.action.play"
            val ACTION_START_SERVICE="com.example.ungdungngenhac.action.start.service"
            val ACTION_SHOW_NOTIFICATION="com.example.ungdungngenhac.action.showNotification"
            val ACTION_BROADCAST="com.example.ungdungngenhac.action.actionBroadcast"
            val ACTION_MAIN="com.example.ungdungngenhac.action.main.action"
        }
    }
    interface NOTIFICATION_ID{
        companion object{
            val FOREGROUND_SERVICE = 123

        }
    }
    companion object{
        val KEY_INTENT_BROADCAST = "actionExtra"
        val KEY_INTENT_SONG_MAIN = "songMain"
        const val IS_ONLINE = "SONG_ONLINE"
        const val IS_LIKE = "SONG_LIKE"

    }

}