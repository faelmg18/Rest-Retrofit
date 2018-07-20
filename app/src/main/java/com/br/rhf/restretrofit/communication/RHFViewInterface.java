package com.br.rhf.restretrofit.communication;

import java.io.Serializable;

public interface RHFViewInterface extends Serializable{
    void runOnUiThread(Runnable action);
}
