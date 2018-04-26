package edu.colorado.plv.droidstar;

import java.util.List;
import java.util.Queue;

public interface MealyTeacher {
    public List<String> inputSet();
    public Queue<String> membershipQuery(Queue<String> query);
}
