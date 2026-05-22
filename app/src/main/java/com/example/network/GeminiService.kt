package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.example.BuildConfig
import java.io.IOException
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    private val systemInstructionContent = GeminiContent(
        parts = listOf(
            GeminiPart(
                text = "Sen Türkiye florası, endemik bitkileri ve yerel baharatları konusunda uzman, kibar ve cana yakın bir yapay zeka botanikçisisin. Kullanıcılara Türk endemik bitkilerinin korunması, özellikleri, ekolojik önemi ve evde veya bahçede yetiştirilmesi için gerekli sulama, toprak, ışık gibi pratik bilgileri Türkçe dilinde açıklayıcı, heveslendirici ve akıcı bir tonla anlatırsın. " +
                        "Cevaplarını her zaman düzenli, çok uzun olmayan paragraflar ve estetik listeler halinde sunarsın. Eğer kullanıcı Türkiye florası, bitkiler, topraklar veya bahçecilik dışı bir konu sorar ise kibarca sadece bu alanda yardımcı olabileceğini hatırlat."
            )
        )
    )

    suspend fun getBotResponse(history: List<Pair<String, Boolean>>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Yapılandırma hatası: Gemini API anahtarı ayarlanmamış. Lütfen AI Studio Secrets panelinden GEMINI_API_KEY değerini girin."
        }

        // Map chat history to Gemini API format
        val contents = history.map { (text, isUser) ->
            GeminiContent(
                role = if (isUser) "user" else "model",
                parts = listOf(GeminiPart(text = text))
            )
        }

        val requestPayload = GeminiRequest(
            contents = contents,
            systemInstruction = systemInstructionContent
        )

        val jsonString = requestAdapter.toJson(requestPayload)
        val requestBody = jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())

        val url = "$BASE_URL?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string()
                if (!response.isSuccessful || bodyString == null) {
                    val code = response.code
                    Log.e(TAG, "API call failed with status code $code. Response: $bodyString")
                    return@withContext "Bot bir hata ile karşılaştı (Hata Kodu: $code). Lütfen biraz sonra tekrar deneyin."
                }

                val apiResponse = responseAdapter.fromJson(bodyString)
                val textResponse = apiResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (textResponse != null) {
                    textResponse
                } else {
                    Log.e(TAG, "Empty or unexpected response content: $bodyString")
                    "Botanist AI şu anda cevap üretemedi. Lütfen tekrar sormayı deneyin."
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network exception: ${e.message}", e)
            "Ağ hatası: Lütfen internet bağlantınızı kontrol edip tekrar deneyin."
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception: ${e.message}", e)
            "Beklenmeyen bir hata oluştu: ${e.localizedMessage}"
        }
    }
}
