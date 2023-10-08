package com.darvader.smarthome.matrix.activity

import android.R
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

class LiveScoreActivity : AppCompatActivity() {

    private var matchSeries: JSONObject = JSONObject()
    private var matchesPayload: JSONObject = JSONObject()
    private lateinit var binding: ActivityLiveScoreBinding
    private lateinit var webSocketClient: WebSocketClient
    val items = ArrayList<String>()
    val matches = ArrayList<Match>()
    val matchesMap = HashMap<String, Match>()
    var match: Match? = null
    val leagues = ArrayList<League>()
    val leaguesMap = HashMap<String, League>()
    var selectedLeague: League? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLiveScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initWebSocket()

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
    }

    override fun onPause() {
        super.onPause()
        webSocketClient.close()
    }

    private fun initWebSocket() {
        val coinbaseUri: URI? = URI(WEB_SOCKET_URL)

        createWebSocketClient(coinbaseUri)

        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        webSocketClient.setSocketFactory(socketFactory)
        webSocketClient.connect()
    }

    private fun createWebSocketClient(coinbaseUri: URI?) {
        webSocketClient = object : WebSocketClient(coinbaseUri) {

            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")
                subscribe()
            }

            override fun onMessage(message: String?) {
                readMessage(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "onError: ${ex?.message}")
            }

        }
    }

    private fun subscribe() {
        webSocketClient.send(
            "{\n" +
                    "    \"type\": \"subscribe\",\n" +
                    "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-EUR\"] }]\n" +
                    "}"
        )
    }

    private fun readMessage(message: String?) {
        message?.let {
            val json = JSONObject(message)
            val type = json.getString("type")
            val payload = json.getJSONObject("payload")
            parseMatches(type, payload)
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
                }
            }
        }
    }

    private fun parseMatches(type: String, payload: JSONObject) {
        if (type == "FETCH_ASSOCIATION_TICKER_RESPONSE") {
            Log.d(TAG, "read FETCH_ASSOCIATION_TICKER_RESPONSE")
            this.matchesPayload = payload
            this.matchSeries = matchesPayload.getJSONObject("matchSeries")
            if (matches.size > 0) return
            val matchDays = payload.getJSONArray("matchDays")
            (0 until matchDays.length()).forEach {
                val matchDay = matchDays.getJSONObject(it)
                val dateTime = ZonedDateTime.parse(matchDay.getString("date"))
                if (dateTime.toLocalDate().equals(LocalDate.now())) {
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

        this.matches.add(match)
    }

    private fun showSets() {
        var result = ""
        match?.matchSets?.forEach {
            result += "${it.team1}:${it.team2}(${it.setNumber} )"
        }
        runOnUiThread { binding.result.text = "$result" }
    }

    companion object {
        // const val WEB_SOCKET_URL = "wss://ws-feed.pro.coinbase.com"
        const val WEB_SOCKET_URL = "wss://backend.sams-ticker.de/dvv"
        const val TAG = "Coinbase"
    }
}