package com.artsoft.stock.model;

import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.util.MathOperation;
import lombok.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Depth {

    private Map<Double, Level> levelMap = new HashMap<>();

    private synchronized Map<Double, Level> levelMap(Share share){
        int max = (int)(MathOperation.arrangeDouble(MathOperation.max(share.getStartPrice())) * 100);
        int min = (int)(MathOperation.arrangeDouble(MathOperation.min(share.getStartPrice())) * 100);

        share.setMax(MathOperation.arrangeDouble((double)max/100));
        share.setMin(MathOperation.arrangeDouble((double)min/100));

        for (int i=min; i<=max; i++){
            Double value = MathOperation.arrangeDouble((double)i/100);
            levelMap.put(value, new Level(value));
        }
        return levelMap;
    }

    public Map<Double, Level> getLevelMap(Share share) {
        return levelMap(share);
    }
}
