package lt.hogfood.hogfood.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"
    private const val RECOMMEND_URL = "https://svc-recommender-latest.onrender.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val foodApi: FoodApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FoodApi::class.java)

    val recommendApi: RecommendApi = Retrofit.Builder()
        .baseUrl(RECOMMEND_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RecommendApi::class.java)

    val interactionApi: InteractionApi = Retrofit.Builder()
        .baseUrl(RECOMMEND_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(InteractionApi::class.java)
}