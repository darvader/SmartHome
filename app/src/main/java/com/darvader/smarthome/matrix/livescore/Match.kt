package com.darvader.smarthome.matrix.livescore

import org.json.JSONObject

data class Match(val matchJSON: JSONObject) {
    val teamDescription1 = matchJSON.getString("teamDescription1")

    val team1 = matchJSON.getString("team1")
    val teamDescription2 = matchJSON.getString("teamDescription2")
    val team2 = matchJSON.getString("team2")
    val id = matchJSON.getString("id")
    val league = matchJSON.getString("matchSeries")

    var setPointsTeam1: Int = 0
    var setPointsTeam2: Int = 0
    var started: Boolean = false
    var finished: Boolean = false
    var matchSets = ArrayList<MatchSet>()
    var leftTeamServes: Boolean = true
    fun update(matchesPayload: JSONObject) {
        val matchStates = matchesPayload.getJSONObject("matchStates")
        val match = matchStates.optJSONObject(id) ?: return
        started = match.getBoolean("started")
        finished = match.getBoolean("finished")


        if (started) {
            val setPoints = match.getJSONObject("setPoints")
            setPointsTeam1 = setPoints.getInt("team1")
            setPointsTeam2 = setPoints.getInt("team2")

            parseSets(match)

        }
    }

    private fun parseSets(match: JSONObject) {
        val matchSetsJson = match.getJSONArray("matchSets")
        matchSets.clear()
        leftTeamServes = match.getString("serving") == "team1"
        (0 until matchSetsJson.length()).forEach {
            val matchSet = MatchSet(matchSetsJson.getJSONObject(it))
            this.matchSets.add(matchSet)
        }
    }

    fun updateMatch(payload: JSONObject) {
        parseSets(payload)
    }
}
