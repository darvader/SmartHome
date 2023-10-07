package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.darvader.smarthome.databinding.ActivityLiveScoreBinding
import com.darvader.smarthome.matrix.BitcoinTicker
import com.darvader.smarthome.matrix.livescore.Match
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.net.ssl.SSLSocketFactory

class LiveScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveScoreBinding
    private lateinit var webSocketClient: WebSocketClient
    val items = ArrayList<String>()
    val matches = ArrayList<Match>()
    var match: Match? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLiveScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initWebSocket()
        binding.games.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                match = matches[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
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
                Log.d(TAG, "onMessage: $message")
                readMessage(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
                unsubscribe()
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

    private fun setUpBtcPriceText(message: String?) {
        message?.let {
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<BitcoinTicker> = moshi.adapter(BitcoinTicker::class.java)
            val bitcoin = adapter.fromJson(message)
            runOnUiThread { binding.btcPriceTv.text = "1 BTC: ${bitcoin?.price} €" }
        }
    }

    private fun readMessage(message: String?) {
        message?.let {
            val json = JSONObject(message)
            val type = json.getString("type")
            val payload = json.getJSONObject("payload")
            if (type == "FETCH_ASSOCIATION_TICKER_RESPONSE") {
                val matchDays = payload.getJSONArray("matchDays")
                (0 until matchDays.length()).forEach {
                    val matchDay = matchDays.getJSONObject(it)
                    val dateTime = ZonedDateTime.parse(matchDay.getString("date"))
                    if (dateTime.toLocalDate().equals(LocalDate.now())) {
                        println(dateTime)

                        val matches = matchDay.getJSONArray("matches")

                        (0 until matchDays.length()).forEach {
                            val matchJSON = matches.getJSONObject(it)
                            val match = Match(matchJSON)
                            this.matches.add(match)
                            val game = "${match.teamDescription1} vs ${match.teamDescription2}"
                            items.add(game)
                            println(game)
                        }
                        runOnUiThread {
                            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
                            binding.games.adapter = adapter
                        }
                    }
                }
            }
            if (type == "MATCH_UPDATE") {
                if (match != null) {
                    val uuid = payload.getString("matchUuid")
                    if (match?.id == uuid) {
                        val sets = payload.getJSONArray("matchSets")
                        var result = ""
                        (0 until sets.length()).forEach {
                            val set = sets.getJSONObject(it)
                            val setScore = set.getJSONObject("setScore")
                            val team1Points = setScore.getString("team1")
                            val team2Points = setScore.getString("team2")
                            result+="$team1Points:$team2Points "
                        }
                        runOnUiThread { binding.btcPriceTv.text = "$result" }
                    }
                }
            }
        }
    }

    private fun unsubscribe() {
        webSocketClient.send(
            "{\n" +
                    "    \"type\": \"unsubscribe\",\n" +
                    "    \"channels\": [\"ticker\"]\n" +
                    "}"
        )
    }

    companion object {
        // const val WEB_SOCKET_URL = "wss://ws-feed.pro.coinbase.com"
        const val WEB_SOCKET_URL = "wss://backend.sams-ticker.de/dvv"
        const val TAG = "Coinbase"
    }
}