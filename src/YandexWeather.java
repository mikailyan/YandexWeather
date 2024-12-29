import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class YandexWeather {

    public static void main(String[] args) {
        final String accessKey = "085c1013-00be-4286-8410-a173abde0fe8";
        final double lat = 55.75;
        final double lon = 37.62;
        final String urlString = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon;

        try {
            // Создаем соединение
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-Weather-Key", accessKey);

            // Читаем ответ
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Парсим JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            System.out.println(jsonResponse);

            // Текущая температура
            if (jsonResponse.has("fact")) {
                JSONObject fact = jsonResponse.getJSONObject("fact");
                if (fact.has("temp")) {
                    int temp = fact.getInt("temp");
                    System.out.println("Температура сейчас: " + temp + "°C");
                }
            }

            // Средняя температура за 5 дней
            if (jsonResponse.has("forecasts")) {
                JSONArray forecasts = jsonResponse.getJSONArray("forecasts");
                int limit = 5;
                double totalTemp = 0;
                int count = 0;

                for (int i = 0; i < Math.min(forecasts.length(), limit); i++) {
                    JSONObject forecast = forecasts.getJSONObject(i);

                    if (forecast.has("parts")) {
                        JSONObject parts = forecast.getJSONObject("parts");

                        JSONArray keys = parts.names();
                        for (int j = 0; j < keys.length(); j++) {
                            String key = keys.getString(i);
                            JSONObject part = parts.getJSONObject(key);
                            if (part.has("temp_min") && part.has("temp_max")) {
                                totalTemp += part.getDouble("temp_min") + part.getDouble("temp_max");
                                count += 2;
                            }
                        }
                    }
                }

                if (count > 0) {
                    double averageTemp = totalTemp / count;
                    System.out.printf("Средняя температура за %d дней: %.2f°C%n", limit, averageTemp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
