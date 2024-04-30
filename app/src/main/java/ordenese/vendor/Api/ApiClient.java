package ordenese.vendor.Api;

import ordenese.vendor.common.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ApiClient {

    private static Retrofit retrofit;
    public static String base_url = Constant.base_url;

    public static Retrofit getClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.cache(null);

        httpClient.connectTimeout(120, TimeUnit.SECONDS) // connect timeout
                .writeTimeout(120, TimeUnit.SECONDS) // write timeout
                .readTimeout(120, TimeUnit.SECONDS); // read timeout

        httpClient.addInterceptor(logging);

        if (retrofit == null) {

            String apiUrl = Constant.base_url;
            retrofit = new Retrofit.Builder()
                    .baseUrl(apiUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(httpClient.build())
                    .build();

        }

        return retrofit;

    }

}
