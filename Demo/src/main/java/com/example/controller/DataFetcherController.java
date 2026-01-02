package com.example.Controller;

import com.example.data.DataCollection;

public interface DataFetcherController {
    DataCollection fetchData(String source, String param1, String param2, String param3);
}
