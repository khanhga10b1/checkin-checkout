package bv.service;

import bv.domain.ScheduleTask;
import bv.utils.NoParamCallback;

import java.util.List;

public interface CICOService {
    void autoCICO(List<ScheduleTask> tasks);

    void autoCICO(List<ScheduleTask> tasks, NoParamCallback callback);

    boolean checkinCheckoutWithToken(String token);

    String preflight(String userName);

    String encodePassword(String secret, String password);

    void checkinCheckoutWithUser(String userName, String password);
}
