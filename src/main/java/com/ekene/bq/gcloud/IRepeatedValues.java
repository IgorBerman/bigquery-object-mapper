package com.ekene.bq.gcloud;

import java.util.List;

public interface IRepeatedValues<T> {

    List<T> getValues();

    void setValues(List<T> values);

}
