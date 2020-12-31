package com.tvcs.homematic;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class LoadCCUData extends AsyncTask<Object, Integer, Throwable> {
    MainActivity mActivity;

    @Override
    protected Throwable doInBackground(Object... params)
    {
        mActivity = (MainActivity) params[0];

        try
        {
            HomeMatic.LoadData(mActivity);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return t;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Throwable throwable) {
        super.onPostExecute(throwable);

        if(throwable != null)
        {
            mActivity.showMessage(throwable.getMessage(), Toast.LENGTH_LONG);
        }
    }
}
