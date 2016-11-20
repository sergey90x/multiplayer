package ru.mail.park.pinger;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;
import ru.mail.park.pinger.requests.PingData;

/**
 * Created by Solovyev on 05/04/16.
 */
public interface PingService {

    void rememberPing(@NotNull Long userName, long clientTimestamp, Id<PingData.Request> requestId);

    TimingData getTimings(@NotNull Long userName);

    void refreshPing(@NotNull Long userName);

}
