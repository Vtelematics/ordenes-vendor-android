package ordenese.vendor.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import ordenese.vendor.common.Constant;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientGson {

   private static Retrofit retrofit;
   public static String base_url = Constant.base_url;

   public static Retrofit getClient() {

      OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      httpClient.cache(null);
     httpClient.addInterceptor(logging);

      Gson gson = new GsonBuilder()
              .setLenient()
              .create();

      if (retrofit == null) {
         String apiUrl = base_url;
         retrofit = new Retrofit.Builder()
                 .baseUrl(apiUrl)
                 .addConverterFactory(GsonConverterFactory.create(gson))
                 .client(httpClient.build())
                 .build();
      }
      return retrofit;

   }

}
