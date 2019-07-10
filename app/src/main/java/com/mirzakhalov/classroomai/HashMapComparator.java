package com.mirzakhalov.classroomai;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

class HashMapComparator implements Comparator<HashMap<String, Object>>
{
    private final String key;

    public HashMapComparator(String key)
    {
        this.key = key;
    }

    public int compare(HashMap<String, Object> first,
                       HashMap<String, Object> second)
    {
        // TODO: Null checking, both for maps and values
        long firstValue = (long)first.get(key);
        long secondValue = (long)second.get(key);
        if(secondValue == firstValue){
            long time1 = (long) first.get("time");
            long time2 = (long) second.get("time");
                    return (int) (time1 - time2);
        }
        return (int) (secondValue - firstValue);
    }
}
