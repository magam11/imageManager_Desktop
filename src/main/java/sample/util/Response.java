package sample.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Response {
    private int responseCode;
    private JSONObject data;
//private JsonObject data;
}
