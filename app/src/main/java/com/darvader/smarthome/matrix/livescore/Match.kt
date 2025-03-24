package com.darvader.smarthome.matrix.livescore

import org.json.JSONObject

data class Match(val matchJSON: JSONObject) {
    val teamDescription1 = matchJSON.getString("teamDescription1").replace("ü", "ue").replace("ü", "ue")
        .replace("ö", "oe")
        .replace("ä", "ae")
        .replace("ß", "ss")

    val team1 = matchJSON.getString("team1")
    val teamDescription2 = matchJSON.getString("teamDescription2").replace("ü", "ue")
        .replace("ö", "oe")
        .replace("ä", "ae")
        .replace("ß", "ss")
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
            parseSets(match)
        }
    }

    private fun parseSets(match: JSONObject) {
        val setPoints = match.getJSONObject("setPoints")
        setPointsTeam1 = setPoints.getInt("team1")
        setPointsTeam2 = setPoints.getInt("team2")

        val size = matchSets.size
        var lastSet: MatchSet? = null
        if (size>0)
          lastSet = matchSets[size - 1]
        val matchSetsJson = match.getJSONArray("matchSets")
        matchSets.clear()
        (0 until matchSetsJson.length()).forEach {
            val matchSet = MatchSet(matchSetsJson.getJSONObject(it))
            this.matchSets.add(matchSet)
        }
        if (size > 0 && lastSet != null) {
            val newLastSet = matchSets[size - 1]
            leftTeamServes = newLastSet.team1 > lastSet.team1
        }
    }

    fun updateMatch(payload: JSONObject) {
        parseSets(payload)
    }
}
