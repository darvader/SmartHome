package com.darvader.smarthome.matrix.activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.databinding.ActivityLiveScoreBinding
import com.darvader.smarthome.matrix.livescore.League
import com.darvader.smarthome.matrix.livescore.Match
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.net.ssl.SSLSocketFactory
import okhttp3.OkHttpClient
import okhttp3.Request


class LiveScoreActivity : AppCompatActivity() {

    companion object {
        const val URL_DVV = "https://backend.sams-ticker.de/live/tickers/dvv"
        const val URL_TVV = "https://backend.sams-ticker.de/live/tickers/tvv"
        const val WEB_SOCKET_URL_DVV = "wss://backend.sams-ticker.de/dvv"
        const val WEB_SOCKET_URL_TVV = "wss://backend.sams-ticker.de/tvv"
        const val TAG = "Coinbase"
        var matchSeries: JSONObject = JSONObject()
        var matchesPayload: JSONObject = JSONObject()
        var selectedRegion: String = "https://backend.sams-ticker.de/live/tickers/tvv"
        var selectedRegionWebsocket: String = "wss://backend.sams-ticker.de/tvv"
        lateinit var binding: ActivityLiveScoreBinding
        var webSocketClient: WebSocketClient? = null
        var scoreboardActivity: ScoreboardActivity? = null
        var livescoreActivity: LiveScoreActivity? = null
        val matches = ArrayList<Match>()
        val matchesMap = HashMap<String, Match>()
        var match: Match? = null
        val leagues = ArrayList<League>()
        val leaguesMap = HashMap<String, League>()
        var selectedLeague: League? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLiveScoreBinding.inflate(layoutInflater)
        livescoreActivity = this
        setContentView(binding.root)
        val regions = arrayOf(URL_TVV, URL_DVV)
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, regions)
        binding.region.adapter = adapter

        binding.region.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val region = regions[position]
                selectedRegion = region
                if (selectedRegion.contains("tvv"))
                    selectedRegionWebsocket = WEB_SOCKET_URL_TVV
                else
                    selectedRegionWebsocket = WEB_SOCKET_URL_DVV
                readMatches();
                matches.clear()
                leagues.clear()
                leaguesMap.clear()
                webSocketClient?.close()
                reInitWebSocket()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.leagues.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val league = leagues[position]
                selectedLeague = league
                val matchNames = ArrayList<String>()
                league.matches.forEach { matchNames.add("${it.teamDescription1}:${it.teamDescription2}") }
                runOnUiThread {
                    val adapter = ArrayAdapter(this@LiveScoreActivity, R.layout.simple_spinner_item, matchNames)
                    binding.matches.adapter = adapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.matches.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                match = selectedLeague?.matches?.get(position)
                match?.update(matchesPayload)
                showSets()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.startScoreboard.setOnClickListener {
            val intent = Intent(this, ScoreboardActivity::class.java)
            startActivity(intent)
        }
    }

    fun fetchJsonFromUrl(url: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                println("Request failed with code: ${response.code}")
                null
            }
        } catch (e: Exception) {
            println("Error fetching JSON: ${e.message}")
            null
        }
    }

    private fun readMatches() {
        Thread {
            val message = fetchJsonFromUrl(selectedRegion)
            val json = JSONObject(message)
            parseMatches(json)
        }.start()
    }

    fun reInitWebSocket() {
        if (null != webSocketClient) {
            if (webSocketClient!!.isOpen)
                return
        }

        val uri: URI? = URI(selectedRegionWebsocket)
        createWebSocketClient(uri)

        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        webSocketClient?.setSocketFactory(socketFactory)
        webSocketClient?.connect()

    }

    private fun createWebSocketClient(uri: URI?) {
        webSocketClient = object : WebSocketClient(uri) {

            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")
                // subscribe()
            }

            override fun onMessage(message: String?) {
                readMessage(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
                reInitWebSocket()
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "onError: ${ex?.message}")
            }

        }
    }

    private fun readMessage(message: String?) {
        message?.let {
            val json = JSONObject(message)
            val type = json.getString("type")
            val payload = json.getJSONObject("payload")
            matchUpdate(type, payload)
        }
    }

    private fun matchUpdate(type: String?, payload: JSONObject) {
        if (type == "MATCH_UPDATE") {
            Log.d(TAG, "read MATCH_UPDATE")

            if (match != null) {
                val uuid = payload.getString("matchUuid")
                if (match?.id == uuid) {
                    match?.updateMatch(payload)
                    showSets()
                    scoreboardActivity?.inform()
                }
            }
        }
    }

    private fun parseMatches(payload: JSONObject) {
        matchesPayload = payload
        matchSeries = payload.getJSONObject("matchSeries")
        val matchDays = payload.getJSONArray("matchDays")
        (0 until matchDays.length()).forEach {
            val matchDay = matchDays.getJSONObject(it)
            val dateTime = ZonedDateTime.parse(matchDay.getString("date"))
            if (dateTime.toLocalDate().equals(LocalDate.now().plusDays(0))) {
                println(dateTime)

                val matches = matchDay.getJSONArray("matches")

                (0 until matches.length()).forEach {
                    parseMatch(matches, it)
                }
            }
        }
        val leagueNames = ArrayList<String>()
        leagues.forEach { leagueNames.add("${it.name}") }
        runOnUiThread {
            val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, leagueNames)
            binding.leagues.adapter = adapter
        }
    }

    private fun parseMatch(matches: JSONArray, it: Int) {
        val matchJSON = matches.getJSONObject(it)
        val match = Match(matchJSON)
        match.update(matchesPayload)
        matchesMap[match.id] = match

        var league: League? = leaguesMap[match.league]
        if (league != null) {
            league = leaguesMap[match.league]
        } else {
            val leagueJSON = matchSeries.getJSONObject(match.league)
            league = League(leagueJSON)
            leaguesMap[match.league] = league
            leagues.add(league)
        }
        league!!.matchesMap[match.id] = match
        league.matches.add(match)

        LiveScoreActivity.matches.add(match)
    }

    private fun showSets() {
        var result = ""
        match?.matchSets?.forEach {
            result += "${it.team1}:${it.team2}(${it.setNumber} )"
        }
        runOnUiThread { binding.result.text = "$result" }
    }
}