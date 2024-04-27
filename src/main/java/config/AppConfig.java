package config;

import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.NoArgsConstructor;
import utils.Assert;

@NoArgsConstructor(access = PRIVATE)
public class AppConfig {
    private static Map<String, String> configs;

    public static void initialize(String[] args) {
        Assert.isNull(configs, "Config should only be inititalized once");
        configs = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            String key = args[i];
            String value = args[i + 1];
            configs.put(key, value);
        }
    }

    public static Optional<String> get(String key) {
        return Optional.ofNullable(configs.get(key));
    }
}
