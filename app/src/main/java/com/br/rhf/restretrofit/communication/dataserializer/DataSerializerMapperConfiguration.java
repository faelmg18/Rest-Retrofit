package com.br.rhf.restretrofit.communication.dataserializer;


public class DataSerializerMapperConfiguration {
    private String formatDate;

    public DataSerializerMapperConfiguration(String formatDate) {
        this.formatDate = formatDate;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

}
