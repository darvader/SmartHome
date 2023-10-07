package com.darvader.smarthome.matrix.livescore

import org.json.JSONObject

data class Match(val matchJSON: JSONObject) {
    val id = matchJSON.getString("id")
    val teamDescription1 = matchJSON.getString("teamDescription1")
    val team1 = matchJSON.getString("team1")
    val teamDescription2 = matchJSON.getString("teamDescription2")
    val team2 = matchJSON.getString("team2")
}
