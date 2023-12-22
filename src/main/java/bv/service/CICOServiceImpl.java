package bv.service;

import bv.domain.CICOPayload;
import bv.domain.LoginPayload;
import bv.domain.ScheduleTask;
import bv.utils.ObjectUtils;
import bv.utils.PopupUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.Timer;

import static bv.utils.FileUtils.*;
import static bv.utils.Constant.*;
import static bv.utils.ObjectUtils.*;

public class CICOServiceImpl implements CICOService {

    private final ObjectMapper objectMapper;
    private static CICOServiceImpl instance = null;


    private CICOServiceImpl() {
        objectMapper = new ObjectMapper();
    }

    public static CICOService getInstance() {
        if (instance == null) {
            synchronized (CICOServiceImpl.class) {
                if (instance == null) {
                    instance = new CICOServiceImpl();
                }
            }
        }
        return instance;
    }


    @Override
    public void autoCICO(List<ScheduleTask> tasks) {
        Timer timer = new Timer(true);
        tasks.forEach(task -> scheduleTask(timer, task));
    }

    @Override
    public void checkinCheckoutWithUser(String username, String password) {
        String secret = preflight(username);
        if (ObjectUtils.hasText(secret)) {
            String encodePassword = encodePassword(secret, password);
            loginAndSetData(encodePassword, username);
        }
    }

    @Override
    public boolean checkinCheckoutWithToken(String token) {
        boolean success = false;
        try {
            URL url = new URL(API_ENDPOINT + CICO);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                CICOPayload cicoPayload = new CICOPayload(2, "Thành Lợi, Da Nang, 84236, Vietnam", 16.059612, 108.211176);
                String payload = objectMapper.writeValueAsString(cicoPayload);
                writer.write(payload);
            }


            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                success = true;
                System.out.println("CICO success!");
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    private void scheduleTask(Timer timer, ScheduleTask task) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (!isWeekend() && !nowGtTime(task.getHour(), task.getMinute())) {
                    if(!checkinCheckoutWithToken(loadFromFile(TOKEN_FILE))) {
                        checkinCheckoutWithUser(null, null);
                    } else {
                        PopupUtils.showSuccess();
                    }
                }
            }
        }, getStartTime(task), 24 * 60 * 60 * 1000); // 24 hours interval
    }

    private boolean nowGtTime(int targetHour, int targetMinute) {
        return LocalTime.now().isAfter(LocalTime.of(targetHour, targetMinute, 59));
    }

    private Date getStartTime(ScheduleTask task) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, task.getHour());
        calendar.set(Calendar.MINUTE, task.getMinute());
        calendar.set(Calendar.SECOND, task.getSecond());
        return calendar.getTime();
    }

    private boolean isWeekend() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    @Override
    public String preflight(String userName) {
        String result = "";
        try {
            URL url = new URL(API_ENDPOINT + PREFLIGHT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                String jsonPayload = "{ \"data\": \"" + (nullOrBlank(userName) ? USERNAME_DEFAULT : userName) + "\" }";
                writer.write(jsonPayload);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Map<String, Object> map = objectMapper.readValue(getResponseData(connection), new TypeReference<>() {
                });
                Map<String, String> dataMap = objectMapper.convertValue(map.get("data"), new TypeReference<>() {
                });
                result = dataMap.get("secret");


            } else {
                System.err.println("Pre Error sending data to the server. Response Code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void loginAndSetData(String encryptPassword, String userName) {
        if (ObjectUtils.nullOrBlank(encryptPassword)) {
            return;
        }
        try {
            URL url = new URL(API_ENDPOINT + LOGIN);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                LoginPayload loginPayload = new LoginPayload(false, encryptPassword, ObjectUtils.nullOrBlank(userName) ? USERNAME_DEFAULT : userName);
                String payload = objectMapper.writeValueAsString(loginPayload);
                System.out.println(loginPayload.toString());
                writer.write(payload);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Map<String, Object> tokenMap = objectMapper.readValue(getResponseData(connection), new TypeReference<>() {
                });
                saveToFile(tokenMap.get("access_token").toString(), TOKEN_FILE);
                if(checkinCheckoutWithToken(tokenMap.get("access_token").toString())) {
                    PopupUtils.showSuccess();
                }
            } else {
                System.err.println("Login Error sending data to the server. Response Code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String encodePassword(String secret, String password) {
        String key = "";
        try {
            URL url = new URL(ENCODE_API_ENDPOINT + "/encode?password=" + encodeParameter(ObjectUtils.nullOrBlank(password) ? PASSWORD_DEFAULT : password) + "&key=" + encodeParameter(secret));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                key = getResponseData(connection);
            } else {
                System.err.println("Encode Error sending data to the server. Response Code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }

    private static String encodeParameter(String parameter) {
        return ObjectUtils.getIgnoreException(() -> URLEncoder.encode(parameter, StandardCharsets.UTF_8), "");
    }


    private String getResponseData(HttpURLConnection connection) {
        return ObjectUtils.getIgnoreException(() -> {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder responseData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseData.append(line);
            }
            return responseData.toString();
        }, "");
    }
}
