package com.example.utils;

import com.example.model.DataPoint;
import com.example.dto.DataPointDTO;
import java.util.List;
import java.util.stream.Collectors;

public class DataPointConverter {
    public static List<DataPointDTO> toDTO(List<DataPoint> points) {
        return points.stream()
                     .map(p -> new DataPointDTO(p.getMonth().toString(), p.getValue(), p.getLabel()))
                     .collect(Collectors.toList());
    }
}
