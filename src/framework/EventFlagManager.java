package framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventFlagManager {

    private final Set<String> flags = new HashSet<>();
    private final Map<String, Integer> variables = new HashMap<>();

    public void setFlag(String flag) {
        flags.add(flag);
    }

    public void clearFlag(String flag) {
        flags.remove(flag);
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public void setVariable(String key, int value) {
        variables.put(key, value);
    }

    public int getVariable(String key) {
        return variables.getOrDefault(key, 0);
    }
}
