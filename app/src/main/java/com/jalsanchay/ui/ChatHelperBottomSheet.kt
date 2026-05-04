package com.jalsanchay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jalsanchay.R
import com.jalsanchay.data.AiService
import com.jalsanchay.data.models.Content
import com.jalsanchay.data.models.GeminiRequest
import com.jalsanchay.data.models.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatHelperBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_ai_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvHistory = view.findViewById<TextView>(R.id.tv_chat_history)
        val etInput = view.findViewById<EditText>(R.id.et_chat_input)
        val btnSend = view.findViewById<FloatingActionButton>(R.id.fab_send)

        btnSend.setOnClickListener {
            val userText = etInput.text.toString().trim()
            if (userText.isNotEmpty()) {
                val currentText = tvHistory.text.toString()
                tvHistory.text = "$currentText\n\nYou: $userText\n\nAI: Thinking..."
                etInput.text.clear()

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val prompt = "You are a helpful water conservation AI assistant for an Indian app called Jal-Sanchay. The user asks: $userText. Give a short, helpful, practical answer in 2-3 sentences."
                        val request = GeminiRequest(
                            contents = listOf(Content(parts = listOf(Part(text = prompt))))
                        )
                        val response = AiService.api.generateContent(AiService.API_KEY, request)
                        val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm not sure right now, but keeping your gutters clean is always a good idea!"
                        
                        withContext(Dispatchers.Main) {
                            tvHistory.text = tvHistory.text.toString().replace("AI: Thinking...", "AI: $reply")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            tvHistory.text = tvHistory.text.toString().replace("AI: Thinking...", "AI: I seem to have lost connection (${e.message}). Remember to check your runoff coefficient!")
                        }
                    }
                }
            }
        }
    }
}
