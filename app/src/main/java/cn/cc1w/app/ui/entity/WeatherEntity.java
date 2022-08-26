package cn.cc1w.app.ui.entity;

/**
 * 天气预报
 * @author kpinfo
 */
public class WeatherEntity {
    private int code;
    private String message;
    private boolean success;
    private WeatherInfo data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public WeatherInfo getData() {
        return data;
    }

    public void setData(WeatherInfo data) {
        this.data = data;
    }

    public static class WeatherInfo {
        private String currentCity;
        private String date;
        private String dayPictureUrl;
        private String nightPictureUrl;
        private String weather;
        private String wind;
        private String temperature;

        public String getCurrentCity() {
            return currentCity;
        }

        public void setCurrentCity(String currentCity) {
            this.currentCity = currentCity;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDayPictureUrl() {
            return dayPictureUrl;
        }

        public void setDayPictureUrl(String dayPictureUrl) {
            this.dayPictureUrl = dayPictureUrl;
        }

        public String getNightPictureUrl() {
            return nightPictureUrl;
        }

        public void setNightPictureUrl(String nightPictureUrl) {
            this.nightPictureUrl = nightPictureUrl;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getWind() {
            return wind;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }
    }
}