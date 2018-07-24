package com.br.rhf.testapirestapp.communication;

import android.app.ProgressDialog;

import com.br.rhf.restretrofit.communication.AbstractAPIClient;
import com.br.rhf.restretrofit.communication.RHFViewInterface;

public class BaseApiCliente<T> extends AbstractAPIClient<T> {

    private ProgressDialog dialog;

    public BaseApiCliente(RHFViewInterface activity) {
        super(activity);
    }

    @Override
    protected String getBaseUrl() {
        return "https://api.github.com/";
    }

    @Override
    protected void onStart() throws Exception {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Aguarde...");
        dialog.show();
    }

    @Override
    protected void onEnd() throws Exception {
        dialog.dismiss();
    }

}
