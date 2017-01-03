package com.ekene.bq.pojos;


import com.ekene.bq.gcloud.IRepeatedRecord;
import java.util.ArrayList;
import java.util.List;

public class NestedPojoRecord implements IRepeatedRecord<NestedPojo> {

    private List<NestedPojo> records = new ArrayList<>();

    @Override
    public List<NestedPojo> getRecords() {
        return records;
    }

    @Override
    public void setRecords(List<NestedPojo> records) {
        this.records = records;
    }

}