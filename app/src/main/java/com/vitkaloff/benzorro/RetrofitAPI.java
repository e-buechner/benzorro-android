package com.vitkaloff.benzorro;
import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.vitkaloff.benzorro.BenzorroAPI.*;

public class RetrofitAPI extends Application {

    private static BenzorroAPI benzorroAPI;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-dev.benzorro.ru/api/") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        benzorroAPI = retrofit.create(BenzorroAPI.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static BenzorroAPI getApi() {
        return benzorroAPI;
    }
}