package com.ekene.bq.gcloud;

import java.util.List;

public interface IRepeatedRecord<T> {

    List<T> getRecords();

    void setRecords(List<T> records);

}
