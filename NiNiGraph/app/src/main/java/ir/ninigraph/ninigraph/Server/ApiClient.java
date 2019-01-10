package ir.ninigraph.ninigraph.Server;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL="http://photojavad.ir/app_server/";

    private static Retrofit retrofit = null;

    public static Retrofit getApi(){

        if (retrofit == null){

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://photojavad.ir/app_server/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
