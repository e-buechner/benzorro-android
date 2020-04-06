package com.vitkaloff.benzorro.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Пока недоступно. Следите за обновлениями!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}