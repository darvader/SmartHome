package com.darvader.smarthome.matrix.activity

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
import com.darvader.smarthome.matrix.livescore.MatchDataService
import com.darvader.smarthome.matrix.livescore.MatchManager
import com.darvader.smarthome.matrix.livescore.LiveScoreWebSocketManager
import org.json.JSONObject

class LiveScoreActivity : AppCompatActivity(),
    MatchDataService.MatchDataListener,
    MatchManager.MatchManagerListener,
    LiveScoreWebSocketManager.WebSocketListener {

    companion object {
        const val URL_DVV = "https://backend.sams-ticker.de/live/indoor/tickers/dvv"
        const val URL_TVV = "https://backend.sams-ticker.de/live/indoor/tickers/tvv"
        const val TEST_MODE = "TEST_MODE"
        const val TAG = "LiveScoreActivity"

        var scoreboardActivity: ScoreboardActivity? = null
        var livescoreActivity: LiveScoreActivity? = null
        var match: Match? = null
        var selectedLeague: League? = null
    }

    private lateinit var binding: ActivityLiveScoreBinding
    private lateinit var matchDataService: MatchDataService
    private lateinit var matchManager: MatchManager
    lateinit var webSocketManager: LiveScoreWebSocketManager
    var selectedRegion: String = URL_TVV

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLiveScoreBinding.inflate(layoutInflater)
        livescoreActivity = this
        setContentView(binding.root)

        initializeServices()
        setupUI()
    }

    private fun initializeServices() {
        matchDataService = MatchDataService(this)
        matchManager = MatchManager()
        webSocketManager = LiveScoreWebSocketManager()

        // MatchDataService doesn't have setListener - listener is passed directly to fetchMatchData
        matchManager.setListener(this)
        webSocketManager.setListener(this)
    }

    private fun setupUI() {
        val regions = arrayOf(URL_TVV, URL_DVV, TEST_MODE)
        val regionNames = arrayOf("TVV", "DVV", "TEST MODE")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regionNames)
        binding.region.adapter = adapter

        binding.region.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val region = regions[position]
                selectedRegion = region
                loadMatches()
                webSocketManager.disconnect()
                webSocketManager.connect(region)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.leagues.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val league = matchManager.leagues[position]
                selectedLeague = league
                updateMatchesSpinner(league)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.matches.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                match = selectedLeague?.matches?.get(position)
                showSets()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.startScoreboard.setOnClickListener {
            val intent = Intent(this, ScoreboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadMatches() {
        matchDataService.fetchMatchData(selectedRegion, this)
    }

    private fun updateMatchesSpinner(league: League) {
        val matchNames = ArrayList<String>()
        league.matches.forEach {
            matchNames.add("${it.teamDescription1}:${it.teamDescription2}")
        }
        runOnUiThread {
            val adapter = ArrayAdapter(this@LiveScoreActivity, android.R.layout.simple_spinner_item, matchNames)
            binding.matches.adapter = adapter
        }
    }

    private fun showSets() {
        var result = ""
        match?.matchSets?.forEach {
            result += "${it.team1}:${it.team2}(${it.setNumber} )"
        }
        runOnUiThread { binding.result.text = result }
    }

    // MatchDataService.MatchDataListener implementation
    override fun onMatchDataReceived(matchData: JSONObject) {
        matchManager.parseMatches(matchData)
    }

    override fun onMatchDataError(error: String) {
        Log.e(TAG, "Match data error: $error")
        runOnUiThread {
            // Could show error message to user
        }
    }

    // MatchManager.MatchManagerListener implementation
    override fun onMatchesParsed(leagues: List<League>) {
        val leagueNames = ArrayList<String>()
        leagues.forEach { leagueNames.add(it.name) }
        runOnUiThread {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, leagueNames)
            binding.leagues.adapter = adapter
        }
    }

    override fun onMatchUpdated(match: Match) {
        if (Companion.match?.id == match.id) {
            runOnUiThread {
                showSets()
            }
            scoreboardActivity?.inform()
        }
    }

    override fun onParsingError(error: String) {
        Log.e(TAG, "Parsing error: $error")
    }

    // LiveScoreWebSocketManager.WebSocketListener implementation
    override fun onMatchUpdate(payload: JSONObject) {
        matchManager.updateMatch(payload)
    }

    override fun onWebSocketConnected() {
        Log.d(TAG, "WebSocket connected")
    }

    override fun onWebSocketDisconnected() {
        Log.d(TAG, "WebSocket disconnected")
    }

    override fun onWebSocketError(error: String) {
        Log.e(TAG, "WebSocket error: $error")
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.disconnect()
    }
}