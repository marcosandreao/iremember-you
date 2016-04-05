package br.com.simpleapp.rememberyou.api;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by marcos on 05/04/16.
 */
public interface IRememberYou {

    @FormUrlEncoded
    @POST("device")
    Call<ResponseBody> registerDevice(@Field("name") String name, @Field("email")  String email, @Field("device_id")  String device_id);
}
