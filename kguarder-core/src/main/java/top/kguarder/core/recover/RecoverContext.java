package top.kguarder.core.recover;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecoverContext {
    private Fallbacker fallback;
}
