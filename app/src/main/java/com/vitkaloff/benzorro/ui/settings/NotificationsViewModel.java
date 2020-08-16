package com.vitkaloff.benzorro.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Пока недоступно. Следите за обновлениями!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}