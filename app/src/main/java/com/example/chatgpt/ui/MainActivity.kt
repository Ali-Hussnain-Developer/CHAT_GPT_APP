package com.example.chatgpt.ui

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.example.chatgpt.R
import com.example.chatgpt.adapter.MessageAdapter
import com.example.chatgpt.model.Message
import com.example.chatgpt.singletonClasses.MySingleton
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recview: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var editText: EditText
    private lateinit var button: ImageButton
    private lateinit var iv_mic: ImageView
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private val apiurl = "https://api.openai.com/v1/completions"
    private val accessToken = "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"
    private var messageList: MutableList<Message> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recview = findViewById(R.id.recycler_view)
        recview.layoutManager = LinearLayoutManager(this)
        editText = findViewById(R.id.edit_text)
        button = findViewById(R.id.button)
        iv_mic = findViewById(R.id.iv_mic)

        messageAdapter = MessageAdapter(messageList)
        recview.adapter = messageAdapter

        button.setOnClickListener {
            ProcessAI()
        }

        iv_mic.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast.makeText(this, " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                editText.setText(result?.get(0))
            }
        }
    }

    fun ProcessAI() {
        val text = editText.text.toString()
        messageList.add(Message(text, true))
        messageAdapter.notifyItemInserted(messageList.size - 1)
        recview.scrollToPosition(messageList.size - 1)
        editText.text.clear()

        val requestBody = JSONObject()
        try {
            requestBody.put("model", "text-davinci-003")
            requestBody.put("prompt", text)
            requestBody.put("max_tokens", 100)
            requestBody.put("temperature", 1)
            requestBody.put("top_p", 1)
            requestBody.put("frequency_penalty", 0.0)
            requestBody.put("presence_penalty", 0.0)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            apiurl,
            requestBody,
            Response.Listener { response ->
                try {
                    val js = response.getJSONArray("choices")
                    val jsonObject = js.getJSONObject(0)
                    val text = jsonObject.getString("text")
                    messageList.add(Message(text.replaceFirst("\n", "").replaceFirst("\n", ""), false))
                    messageAdapter.notifyItemInserted(messageList.size - 1)
                    recview.scrollToPosition(messageList.size - 1)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("API Error", error.toString())
                messageList.add(Message(error.toString().replaceFirst("\n", "").replaceFirst("\n", ""), false))
                messageAdapter.notifyItemInserted(messageList.size - 1)
                recview.scrollToPosition(messageList.size - 1)
            },
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = "Bearer $accessToken"
                headers["Content-Type"] = "application/json"
                return headers
            }

            override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
                return super.parseNetworkResponse(response)
            }
        }

        val timeoutMs = 25000 // 25 seconds timeout
        val policy: RetryPolicy = DefaultRetryPolicy(
            timeoutMs,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,
        )
        request.retryPolicy = policy

        // Add the request to the RequestQueue
        MySingleton.getInstance(this).addToRequestQueue(request)
    }
}
