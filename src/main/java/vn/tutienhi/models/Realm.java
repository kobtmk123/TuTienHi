package vn.tutienhi.models;

import vn.tutienhi.utils.ChatUtil;
import java.util.List;

public class Realm {
    private final String id;
    private final String displayName;
    private final double maxLinhKhi;
    // ... các trường khác

    public Realm(String id, String displayName, double maxLinhKhi, ...) {
        this.id = id;
        this.displayName = ChatUtil.colorize(displayName);
        // ...
    }

    // ... các getters
}