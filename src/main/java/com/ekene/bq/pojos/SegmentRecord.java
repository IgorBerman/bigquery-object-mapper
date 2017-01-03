package com.ekene.bq.pojos;

import com.ekene.bq.gcloud.IRepeatedRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SegmentRecord implements IRepeatedRecord<Segment> {

    private List<Segment> records = new ArrayList<>();

    @Override
    public List<Segment> getRecords() {
        return records;
    }

    @Override
    public void setRecords(List<Segment> records) {
        this.records = records;
    }

}