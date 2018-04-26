package edu.colorado.plv.droidstar;

import java.util.List;
import java.util.Queue;
import java.lang.Runnable;

public interface AsyncMealyTeacher {
    public List<String> inputSet();
    public void membershipQuery(Runnable r,
                                Queue<String> result,
                                Queue<String> query);
}
